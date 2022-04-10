package com.example.saper;

public enum GameDifficulty {
    Easy{
        public Config GetConfigField(){
            return ConfigLoader.LoadConfig(GameDifficulty.Easy);
        }
    },
    Normal{
        public Config GetConfigField() {
            return ConfigLoader.LoadConfig(GameDifficulty.Normal);
        }
    },
    Hard{
        public Config GetConfigField() {
            return ConfigLoader.LoadConfig(GameDifficulty.Hard);
        }
    };

    public abstract Config GetConfigField();
}
