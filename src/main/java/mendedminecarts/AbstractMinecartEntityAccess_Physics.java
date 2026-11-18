package mendedminecarts;

public interface AbstractMinecartEntityAccess_Physics {
    boolean isSelfMovingOnRail();

    void setSelfMovingOnRail(boolean selfMoving);

    float getCartVelocityMultiplier();

    long getLastPostCollisionTick();

    void setLastPostCollisionTick(long tick);
}
