package game.logic.util.json;

import java.util.ArrayList;

public class WrappedJsonList {
    public ArrayList<Object> list = new ArrayList<>();

    public boolean add(Object object) {
        if(object instanceof WrappedJsonObject json) {
            return this.list.add(json.children);
        }

        return this.list.add(object);
    }

    public Object get(int index) {
        return this.list.get(index);
    }

    public WrappedJsonObject getObject(int index) {
        Object object = this.list.get(index);
        // I don't know why it's already converted, but I'm doing this rather than finding the reason why
        if(object instanceof WrappedJsonObject json) {
            return json;
        }

        WrappedJsonObject json = new WrappedJsonObject();
        json.children = (java.util.HashMap<String, Object>) object;

        return json;
    }
}
