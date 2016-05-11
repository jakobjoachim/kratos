package Board;

import Exceptions.BoardAlreadyExistsException;
import Exceptions.BoardDoesNotExistException;
import Exceptions.PawnDoesNotExistException;

/**
 * Created by Nikoko on 10.05.16.
 */
public class BoardModel {
    public boolean pawnAlreadyPlaced;

    public Object getAllActiveGames() {
        return "";
    }

    public String createBoard(String player) throws BoardAlreadyExistsException {
        return "";
    }

    public Object getBoardInfo(String params) throws BoardDoesNotExistException {
        return "";
    }

    public static void deleteBoard(String id) throws BoardDoesNotExistException {

    }

    public Object getPlayerPositions(String params) throws BoardDoesNotExistException{
        return "";
    }

    public void placePawn(String gameId, String position) {
    }

    public void deletePawn(String position) throws PawnDoesNotExistException{
    }
}
