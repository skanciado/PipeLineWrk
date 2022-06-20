package apb.jenkins.utils

public class Environments {
    public static enum EnvironmentsEnum {
        PRE("preprod"), PRO("pro")
        private final String name;

        private EnvironmentsEnum(String s) {
            name = s;
        }

        public String toString() {
            return this.name;
        }
    } 

}
