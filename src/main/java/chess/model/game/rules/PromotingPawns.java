package chess.model.game.rules;

import chess.model.game.board.SquareBoard;
import chess.model.game.move.Move;
import chess.model.game.move.MoveDecorator;
import chess.model.game.pieces.Pawn;
import chess.model.game.pieces.Piece;
import chess.model.game.pieces.Queen;
import chess.model.game.pieces.movingBehaviours.TwoAndOneStrategy;

import java.util.*;

public class PromotingPawns implements Rule{
    Set<Piece> pieceSet;
    int colToSearchNorth;
    int colToSearchSouth;

    private static class PromotingMove extends MoveDecorator {
        Set<Piece> pieces;
        PromotingMove(Move move, Set<Piece> pieces) {
            super(move);
            this.pieces = pieces;
        }

        @Override
        public void execute() {
            super.execute();
            Queen queen = new Queen(super.getPiece().getColor(),super.getPosition());
            pieces.remove(super.getPiece());
            pieces.add(queen);
        }
    }

    public PromotingPawns(Set<Piece> pieceSet, SquareBoard squareBoard) {
        this.pieceSet = pieceSet;
        colToSearchSouth = squareBoard.getColumns();
        colToSearchNorth = 1;
    }

    @Override
    public void obyRule(Set<Move> movesToFilter, Piece p) {
        if (p instanceof Pawn pawn && pawn.isMoved()) {
            TwoAndOneStrategy.Direction direction = ((TwoAndOneStrategy) pawn.getMovingBehaviour()).getDirection();
            int rowToSearch = direction == TwoAndOneStrategy.Direction.NORTH ? 1:colToSearchSouth;
            Set<Move> toDelete = new HashSet<>();
            Set<Move> toAdd = new HashSet<>();
            for (Move move: movesToFilter) {
                if (move.getPosition().getRow() == rowToSearch) {
                    toDelete.add(move);
                    toAdd.add(new PromotingMove(move, pieceSet));
                }
            }
            for (Move move : toDelete) { movesToFilter.remove(move);}
            movesToFilter.addAll(toAdd);
        }
    }
}
