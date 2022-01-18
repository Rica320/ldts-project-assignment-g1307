package model.game.builder;

import model.game.GameCursor;
import model.game.GameModel;
import model.game.Position;
import model.game.board.SquareBoard;
import model.game.clock.Clock;
import model.game.clock.ClockModel;
import model.game.pieces.*;
import model.game.pieces.movingBehaviours.TwoAndOneStrategy;
import model.game.player.Player;
import model.game.rules.*;

import java.sql.Time;
import java.util.HashSet;
import java.util.Set;

public class StandardChessGame implements GameBuilder {

    private final GameModel gameModel;
    private final Set<Piece> pieceSet;
    private final Set<Piece> piecesArrangementWhite;
    private final Set<Piece> piecesArrangementBlack;
    private Rule[] rules;
    private final SquareBoard squareBoard;

    public StandardChessGame(){
        this(300, 0);
    }

    public StandardChessGame(int time, int increment){
        this.gameModel = new GameModel();
        piecesArrangementWhite = new HashSet<>();
        piecesArrangementBlack = new HashSet<>();

        squareBoard = new SquareBoard(8);
        pieceSet = gameModel.getPiecesInGame();
        gameModel.setCursor(new GameCursor(new Position(1,1) , squareBoard));
        gameModel.setBoardModel(squareBoard);

        Player[] players = {new Player(new ClockModel(time, increment),Piece.COLOR.White), new Player(new ClockModel(time, increment),Piece.COLOR.BLACK)};
        gameModel.setGamePlayers(players);
        rules = new Rule[10];
    }

    @Override
    public GameBuilder reset() {
        piecesArrangementWhite.clear();
        piecesArrangementBlack.clear();
        pieceSet.clear();
        rules = new Rule[0];

        updateModelPieces();
        gameModel.setRules(rules);
        return this;
    }

    public void buildPawns() {
        for (int i = 1; i <= squareBoard.getColumns(); i++){
            piecesArrangementWhite.add(new Pawn(Piece.COLOR.White, new Position(7, i),
                    new TwoAndOneStrategy(TwoAndOneStrategy.Direction.NORTH)));
            piecesArrangementBlack.add(new Pawn(Piece.COLOR.BLACK, new Position(2, i),
                    new TwoAndOneStrategy(TwoAndOneStrategy.Direction.SOUTH)));
        }
        updateModelPieces();

    }

    public void buildKings() {
        piecesArrangementWhite.add(new King(Piece.COLOR.White, new Position(8, 5)));
        piecesArrangementBlack.add(new King(Piece.COLOR.BLACK, new Position(1, 5)));
        updateModelPieces();

    }

    public void buildQueens() {
        piecesArrangementWhite.add(new Queen(Piece.COLOR.White, new Position(8, 4)));
        piecesArrangementBlack.add(new Queen(Piece.COLOR.BLACK, new Position(1, 4)));
        updateModelPieces();

    }

    public void buildRooks() {
        piecesArrangementWhite.add(new Rook(Piece.COLOR.White, new Position(8, 1)));
        piecesArrangementWhite.add(new Rook(Piece.COLOR.White, new Position(8, 8)));
        piecesArrangementBlack.add(new Rook(Piece.COLOR.BLACK, new Position(1, 1)));
        piecesArrangementBlack.add(new Rook(Piece.COLOR.BLACK, new Position(1, 8)));
        updateModelPieces();
    }

    public void buildKnights() {
        piecesArrangementWhite.add(new Knight(Piece.COLOR.White, new Position(8, 2)));
        piecesArrangementWhite.add(new Knight(Piece.COLOR.White, new Position(8, 7)));
        piecesArrangementBlack.add(new Knight(Piece.COLOR.BLACK, new Position(1, 2)));
        piecesArrangementBlack.add(new Knight(Piece.COLOR.BLACK, new Position(1, 7)));
        updateModelPieces();

    }

    public void buildBishops() {
        piecesArrangementWhite.add(new Bishop(Piece.COLOR.White, new Position(8, 3)));
        piecesArrangementWhite.add(new Bishop(Piece.COLOR.White, new Position(8, 6)));
        piecesArrangementBlack.add(new Bishop(Piece.COLOR.BLACK, new Position(1, 3)));
        piecesArrangementBlack.add(new Bishop(Piece.COLOR.BLACK, new Position(1, 6)));
        updateModelPieces();

    }

    @Override
    public GameBuilder buildPieces() {
        buildPawns();
        buildKings();
        buildQueens();
        buildRooks();
        buildKnights();
        buildBishops();
        updateModelPieces();
        return this;
    }

    private void updateModelPieces() {
        pieceSet.addAll(piecesArrangementBlack);
        pieceSet.addAll(piecesArrangementWhite);
    }

    @Override
    public GameBuilder buildRules() {
        rules[0] = new Castle(squareBoard,pieceSet);
        rules[1] = new NoStepOverPiece(squareBoard, pieceSet);
        rules[2] = new KillPieceOnCapture(pieceSet);
        rules[3] = new NotCapturingSameColor(pieceSet);
        rules[4] = new PawnsDiagonalCapturing(pieceSet);
        rules[5] = new PawnsStandardMoveRule(pieceSet);
        rules[6] = new EnPassant(pieceSet);
        rules[7] = new PromotingPawns(pieceSet, squareBoard);
        rules[8] = new ProtectKingRule(gameModel);
        rules[9] = new NoSuicideAllowed(pieceSet,rules,squareBoard);

        gameModel.setRules(rules);
        return this;
    }


    @Override
    public GameModel getResults() {
        return gameModel;
    }
}
