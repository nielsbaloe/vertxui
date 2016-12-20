package j2html;

public abstract class DomContent {

    public abstract String render();

    @Override
    public String toString() {
        return render();
    }

}
