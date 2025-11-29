package entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "point_results")
@NamedQuery(name = "Result.findByUser",
        query = "SELECT p FROM Result p WHERE p.user.id = :userId ORDER BY p.checkTime DESC")
public class Result implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double x;

    @Column(nullable = false)
    private Double y;

    @Column(nullable = false)
    private Double r;

    @Column(nullable = false)
    private Boolean result;

    @Column(name = "check_time", nullable = false)
    private LocalDateTime checkTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        if (checkTime == null) {
            checkTime = LocalDateTime.now();
        }
    }

    public Result() {}

    public Result(Double x, Double y, Double r, Boolean result, User user) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.result = result;
        this.user = user;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }

    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }

    public Double getR() { return r; }
    public void setR(Double r) { this.r = r; }

    public Boolean getResult() { return result; }
    public void setResult(Boolean result) { this.result = result; }

    public LocalDateTime getCheckTime() { return checkTime; }
    public void setCheckTime(LocalDateTime checkTime) { this.checkTime = checkTime; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}