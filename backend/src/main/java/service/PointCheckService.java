package service;

import entity.Result;
import entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class PointCheckService {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public Result checkPoint(Double x, Double y, Double r, User user) {
        boolean result = checkHit(x, y, r);
        Result pointCheck = new Result(x, y, r, result, user);
        em.persist(pointCheck);
        return pointCheck;
    }

    public static boolean checkHit(Double x, Double y, Double r) {
        // Проверка попадания в область
        // 1-я четверть: четверть круга
        if (x >= 0 && y >= 0) {
            return (x*x + y*y) <= (r*r);
        }
        // 2-я четверть: треугольник
        if (x <= 0 && y >= 0) {
            return (y <= x + r/2);
        }
        // 3-я четверть: прямоугольник
        if (x <= 0 && y <= 0) {
            return (x >= -r/2) && (y >= -r);
        }
        // 4-я четверть: нет области
        return false;
    }

    public List<Result> getUserChecks(User user) {
        return em.createQuery("SELECT p FROM Result p WHERE p.user = :user ORDER BY p.checkTime DESC", Result.class)
                .setParameter("user", user)
                .getResultList();
    }
}