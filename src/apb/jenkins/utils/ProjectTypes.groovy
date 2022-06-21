package apb.jenkins.utils

public enum ProjectTypes {
    MAVEN ("maven"),
    DOTNET("dotnet"),
    DOCKER("docker")
    private final String name;

    private ProjectTypes(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }
}
