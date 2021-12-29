package model.menu;

import model.Model;

public class MenuModel implements Model {

    public enum Option {NEWGAME, EXIT;
        static final Option[] values = values();
        public Option next() {
            return values[(this.ordinal()+1) % values.length];
        }

        public Option previous() {
            return values[(this.ordinal()-1 + values.length) % values.length];
        }

        public static int maxLength() {
            int max = Option.NEWGAME.toString().length();
            for (Option p: values) {
                if (p.toString().length() > max)
                    max = p.toString().length();
            }
            return max;
        }

        public int diffToOption(Option option) { // return the difference in index
            return option.ordinal() - this.ordinal();
        }

    } // Same question of Color enum. TODO: add more options
    private Option current;

    public MenuModel() {
        current = Option.NEWGAME;
    }

    public void setNextOption() {
        current = current.next();
    }

    public void setPreviousOption() {
        current = current.previous();
    }

    public Option getCurrentOption() {
        return current;
    }

}
