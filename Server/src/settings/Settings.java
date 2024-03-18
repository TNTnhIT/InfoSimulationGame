package settings;

/**
 * An Interface used for constants when communicating with the server
 */
public interface Settings {
    /**
     * Used to send the friendly strategy to the server
     */
    int SEND_FRIENDLY = 0;
    /**
     * Used to send the aggressive strategy to the server
     */
    int SEND_AGGRESSIVE = 1;
    /**
     * Used to inform the server, that some kind of error occurred
     */
    int SEND_ERROR = -1;
    /**
     * Used by the server, to give the player feedback; that he picked the friendly strategy
     */
    int RECEIVE_FRIENDLY = 0;
    /**
     * Used by the server, to give the player feedback; that he picked the aggressive strategy
     */
    int RECEIVE_AGGRESSIVE = 1;
    /**
     * Used by the server, to inform the player that the other player picked the friendly strategy
     */
    int RECEIVE_OTHER_PLAYER_FRIENDLY = 2;
    /**
     * Used by the server, to inform the player that the other player picked the aggressive strategy
     */
    int RECEIVE_OTHER_PLAYER_AGGRESSIVE = 3;
    /**
     * Used by the server, to inform the player, that the game has started
     */
    int RECEIVE_GAME_START = 4;
    /**
     * Used by the server, to inform the player, that the game has ended
     */
    int RECEIVE_GAME_END = 5;
    /**
     * Used by the server, to inform the player, that the next round has started
     */
    int RECEIVE_NEXT_ROUND = 6;
    /**
     * Used by the server, to inform the player, that his message was incorrect (e.g. if the user sends a value of 3)
     */
    int RECEIVE_WRONG_MESSAGE = -2;
    /**
     * Used by the server, to inform the player that he is sending messages to fast/before the other player
     */
    int RECEIVE_TO_FAST = -3;
    /**
     * Used by the server, to inform the player of any other error that may occur
     */
    int RECEIVE_OTHER_ERROR = -1;

    //The same constants but as an enum, in case someone wants to use those

    public enum SENDER{
        FRIENDLY(Settings.SEND_FRIENDLY),
        AGGRESSIVE(Settings.SEND_AGGRESSIVE),
        ERROR(SEND_ERROR);
        public int num;
        SENDER(int num) {
            this.num = num;
        }
        public static SENDER valueOf(int i) {
           for(SENDER s: SENDER.values())
               if(s.num == i) return s;
           return ERROR;
        }
    }
    public enum RECEIVER {
        FRIENDLY(Settings.RECEIVE_FRIENDLY),
        AGGRESSIVE(Settings.RECEIVE_AGGRESSIVE),
        OTHER_PLAYER_FRIENDLY(Settings.RECEIVE_OTHER_PLAYER_FRIENDLY),
        OTHER_PLAYER_AGGRESSIVE(Settings.RECEIVE_OTHER_PLAYER_AGGRESSIVE),
        GAME_START(Settings.RECEIVE_GAME_START),
        GAME_END(Settings.RECEIVE_GAME_END),
        NEXT_ROUND(Settings.RECEIVE_NEXT_ROUND),
        WRONG_MESSAGE(Settings.RECEIVE_WRONG_MESSAGE),
        TO_FAST(Settings.RECEIVE_TO_FAST),
        OTHER_ERROR(Settings.RECEIVE_OTHER_ERROR);
        public int num;
        RECEIVER(int num) {
            this.num = num;
        }

        public static RECEIVER valueOf(int i) {
           for(RECEIVER r: RECEIVER.values())
               if(r.num == i) return r;
           return OTHER_ERROR;
        }
    }

}
