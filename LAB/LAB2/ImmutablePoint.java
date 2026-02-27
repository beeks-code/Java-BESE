import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

// Immutable class: final class, all fields final, no setters, defensive copies
public final class ImmutablePoint {
    private final double x;
    private final double y;
    private final String label;
    private final List<String> tags; // Mutable collection — must be handled carefully

    public ImmutablePoint(double x, double y, String label, List<String> tags) {
        this.x = x;
        this.y = y;
        this.label = label;
        // Defensive copy to prevent external mutation
        this.tags = Collections.unmodifiableList(new ArrayList<>(tags));
    }

    // Only getters — no setters
    public double getX() { return x; }
    public double getY() { return y; }
    public String getLabel() { return label; }
    public List<String> getTags() { return tags; } // Already unmodifiable

    // "Wither" methods return NEW objects instead of modifying
    public ImmutablePoint withLabel(String newLabel) {
        return new ImmutablePoint(x, y, newLabel, tags);
    }

    public ImmutablePoint translate(double dx, double dy) {
        return new ImmutablePoint(x + dx, y + dy, label, tags);
    }

    public double distanceTo(ImmutablePoint other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return String.format("ImmutablePoint[%s](%.2f, %.2f) tags=%s", label, x, y, tags);
    }

    public static void main(String[] args) {
        List<String> tags = new ArrayList<>(List.of("origin", "reference"));
        ImmutablePoint p1 = new ImmutablePoint(0, 0, "Origin", tags);
        System.out.println("p1: " + p1);

        // Attempting to modify original tags list has NO effect on p1
        tags.add("modified_externally");
        System.out.println("After external list mutation, p1: " + p1);

        // Attempting to modify tags through getter throws exception
        try {
            p1.getTags().add("hacked");
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot modify tags: " + e.getClass().getSimpleName());
        }

        // Creating derived objects — original remains unchanged
        ImmutablePoint p2 = p1.translate(3, 4);
        System.out.println("p2 (translated): " + p2);
        System.out.println("p1 still unchanged: " + p1);
        System.out.printf("Distance p1→p2: %.2f%n", p1.distanceTo(p2));
    }
}
