package game.shared.util.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.PeekWorkaround;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class WrappedJsonObject {
    public HashMap<String, Object> children = new HashMap<>();

    public void put(String name, Object object) {
        if(object instanceof WrappedJsonObject wrappedJsonObject) {
            object = wrappedJsonObject.children;
        } else if(object instanceof WrappedJsonList jsonList) {
            object = jsonList.list;
        }

        this.children.put(name, object);
    }

    public Object get(String name) {
        return this.children.get(name);
    }

    public String getString(String name) {
        return (String) this.get(name);
    }

    public String getStringOrDefault(String name, String defaultValue) {
        if(this.get(name) == null) {
            return defaultValue;
        }

        return this.getString(name);
    }

    public int getInt(String name) {
        if(this.get(name) instanceof Integer) {
            return (Integer) this.get(name);
        }

        return ((Long) this.get(name)).intValue();
    }

    public int getIntOrDefault(String name, int defaultValue) {
        if(this.get(name) == null) {
            return defaultValue;
        }

        return this.getInt(name);
    }

    public float getFloat(String name) {
        return ((Double) this.get(name)).floatValue();
    }

    public double getDouble(String name) {
        return (double) this.get(name);
    }

    public long getLong(String name) {
        return (long) this.get(name);
    }

    public long getLongOrDefault(String name, long defaultValue) {
        if(this.get(name) == null) {
            return defaultValue;
        }

        return this.getLong(name);
    }

    public boolean getBoolean(String name) {
        return (boolean) this.get(name);
    }

    public boolean getBooleanOrDefault(String name, boolean defaultValue) {
        if(this.get(name) == null) {
            return defaultValue;
        }

        return this.getBoolean(name);
    }

    public ArrayList<?> getList(String name) {
        return (ArrayList<?>) this.get(name);
    }

    // Temporary until I figure out a better solution
    public WrappedJsonList getJsonList(String name) {
        WrappedJsonList jsonList = new WrappedJsonList();
        jsonList.list = (ArrayList<Object>) this.get(name);

        return jsonList;
    }

    public WrappedJsonObject getObject(String name) {
        WrappedJsonObject object = new WrappedJsonObject();
        object.children = (HashMap<String, Object>) this.get(name);
        return object;
    }

    public boolean containsKey(String key) {
        return this.children.containsKey(key);
    }

    public int size() {
        return this.children.size();
    }

    public static WrappedJsonObject read(JsonReader reader) {
        WrappedJsonObject object = new WrappedJsonObject();
        try {
            if (!reader.hasNext()) return null;

            JsonToken jsonToken = reader.peek();

            if(jsonToken == JsonToken.BEGIN_OBJECT) {
                reader.beginObject();
                JsonToken nextToken = reader.peek();

                while(nextToken != JsonToken.END_OBJECT) {
                    if(nextToken == JsonToken.END_DOCUMENT) {
                        throw new IllegalStateException("EOF before reaching json object end");
                    } else if(nextToken != JsonToken.NAME) {
                        throw new IllegalStateException("Next token not a name token while inside a json object");
                    }

                    String name = reader.nextName();
                    nextToken = reader.peek();

                    if(nextToken == JsonToken.STRING) {
                        object.put(name, reader.nextString());
                    } else if(nextToken == JsonToken.NULL) {
                        object.put(name, null); // Wait, I don't think this does anything lol
                        reader.nextNull();
                    } else if(nextToken == JsonToken.NUMBER) {
                        // I don't know if it's an int or long, or float or double (problem with the json format) and I gotta use a workaround to get the token id directly (problem with gson not differentiating between long and double using JsonReader.peek())
                        if(PeekWorkaround.peekNextTokenId(reader) == 15) {
                            object.put(name, reader.nextLong());
                        } else {
                            object.put(name, reader.nextDouble());
                        }
                    } else if(nextToken == JsonToken.BOOLEAN) {
                        object.put(name, reader.nextBoolean());
                    } else if(nextToken == JsonToken.BEGIN_OBJECT) {
                        object.put(name, WrappedJsonObject.read(reader));
                    } else if(nextToken == JsonToken.BEGIN_ARRAY) {
                        ArrayList<Object> list = new ArrayList<>();
                        reader.beginArray();
                        nextToken = reader.peek();
                        while(nextToken != JsonToken.END_ARRAY) {
                            if(nextToken == JsonToken.STRING) {
                                list.add(reader.nextString());
                            } else if(nextToken == JsonToken.NUMBER) {
                                // Same problem as above
                                if(PeekWorkaround.peekNextTokenId(reader) == 15) {
                                    list.add(reader.nextLong());
                                } else {
                                    list.add(reader.nextDouble());
                                }
                            } else if(nextToken == JsonToken.BOOLEAN) {
                                list.add(reader.nextBoolean());
                            } else if(nextToken == JsonToken.BEGIN_OBJECT) {
                                list.add(WrappedJsonObject.read(reader));
                            } else {
                                throw new IllegalStateException("Array entry not string, number, boolean or object. Got " + nextToken);
                            }

                            nextToken = reader.peek();
                        }

                        object.put(name, list);

                        reader.endArray();
                    }

                    nextToken = reader.peek();
                }

                reader.endObject();
            } else {
                throw new IllegalStateException("Expected to read a json object, but the next token was " + jsonToken);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed reading json", e);
        }

        return object;
    }

    public JsonElement toElement() {
        return new Gson().toJsonTree(this.children);
    }

    @Override
    public String toString() {
        return this.toElement().toString();
    }
}
