package model;

import java.io.*;

/**
 * 游戏状态类，包含需要保存的所有游戏数据
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private int stepCount;
    private MapModel mapModel;

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public MapModel getMapModel() {
        return mapModel;
    }

    public void setMapModel(MapModel mapModel) {
        this.mapModel = mapModel;
    }


    /**
     * 将游戏状态保存到文件
     */
    public static void saveToFile(GameState state, String path) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(path))) {
            oos.writeObject(state);
        }
    }

    /**
     * 从文件加载游戏状态
     */
    public static GameState loadFromFile(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(path))) {
            return (GameState) ois.readObject();
        }
    }
}