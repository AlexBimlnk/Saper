package com.example.saper;

public enum GameDifficulty {
    Easy{
        public Config GetConfigField(){
            Config config = new Config(100, 50, "Easy");
            config.setCountMines(16);
            return config;
        }
    },
    Normal{
        public Config GetConfigField() {
            Config config = new Config(400, 25, "normal");
            config.setCountMines(64);
            return config;
        }
    },
    Hard{
        public Config GetConfigField() {
            Config config = new Config(400, 25, "hard");
            config.setCountMines(64);
            return config;
        }
    };

    public abstract Config GetConfigField();
}
