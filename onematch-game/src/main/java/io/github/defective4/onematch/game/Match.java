package io.github.defective4.onematch.game;

public interface Match {

    boolean isBoardVisible();

    boolean isFree();

    boolean isMovable();

    void setBoardVisible(boolean b);

    void setFree(boolean b);

}
