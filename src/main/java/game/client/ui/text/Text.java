package game.client.ui.text;

public interface Text {
    class Static implements Text {
        String text;

        public Static(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    class Translated implements Text {
        String key;

        public Translated(String translationKey) {
            this.key = translationKey;
        }

        @Override
        public String toString() {
            return Language.translate(this.key);
        }
    }
}
