package com.example.saper;

public enum GameDifficulty {
    Easy{
        public Config GetConfigField(){
            Config config = new Config(100, 50, 16, "Easy");
            return config;
        }
    },
    Normal{
        public Config GetConfigField() {
            Config config = new Config(400, 25, 64, "normal");
            return config;
        }
    },
    Hard{
        public Config GetConfigField() {
            Config config = new Config(400, 25, 64, "hard");
            return config;
        }
    };

    public abstract Config GetConfigField();
}
