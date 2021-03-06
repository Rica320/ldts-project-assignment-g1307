package chess.model.game.rules

import chess.model.game.GameModel
import chess.model.game.Position
import chess.model.game.board.BoardModel
import chess.model.game.board.SquareBoard
import chess.model.game.move.Move
import chess.model.game.move.SimpleMove
import chess.model.game.pieces.Pawn
import chess.model.game.pieces.Piece
import chess.model.game.pieces.movingBehaviours.TwoAndOneStrategy
import spock.lang.Specification

class PawnsStandardMoveRuleTest extends Specification {
    def "Two pos and then one"() {
        given:
        def piece = Mock(Pawn)

        piece.getPosition() >> new Position(3,3)

        piece.getColor() >> Piece.COLOR.White
        piece.getMovingBehaviour() >> new TwoAndOneStrategy(TwoAndOneStrategy.Direction.NORTH)
        Set<Move> set = new HashSet()
        set.add(new SimpleMove(piece ,new Position(2,3)))
        set.add(new SimpleMove(piece ,new Position(1,3)))
        piece.getMoves(_ as BoardModel) >> set

        def s = new HashSet()
        s.add(piece)

        BoardModel boardModel = Mock(SquareBoard)

        boardModel.positionInBoard(_ as Position) >> true

        GameModel gameModel = new GameModel()
        gameModel.setBoardModel(boardModel)
        gameModel.setPiecesInGame(s)
        def nC = new PawnsStandardMoveRule(gameModel.getPiecesInGame())

        when:
        Set<Move> r = piece.getMoves(gameModel.getBoardModel())
        nC.obyRule(r, piece)
        def ls = r.size()
        piece.isMoved() >> true
        nC.obyRule(r, piece)
        def rs = r.size()

        then:
        ls == 2
        rs == 1
    }

    def "Pawns capturing with move in front bug"() {
        given:
        def piece = Mock(Pawn)
        def enemyPiece1 = Mock(Pawn)
        def enemyPiece2 = Mock(Pawn)

        piece.getPosition() >> new Position(7,3)
        enemyPiece1.getPosition() >> new Position(5,3)
        enemyPiece2.getPosition() >> new Position(6,3)

        piece.getColor() >> Piece.COLOR.White
        piece.getMovingBehaviour() >> new TwoAndOneStrategy(TwoAndOneStrategy.Direction.NORTH)
        Set<Move> set = new HashSet()
        set.add(new SimpleMove(piece ,new Position(5,3)))
        set.add(new SimpleMove(piece ,new Position(6,3)))
        piece.getMoves(_ as BoardModel) >> set

        def s = new HashSet()
        s.add(piece)
        s.add(enemyPiece1)
        s.add(enemyPiece2)

        BoardModel boardModel = Mock(SquareBoard)

        boardModel.positionInBoard(_ as Position) >> true

        GameModel gameModel = new GameModel()
        gameModel.setBoardModel(boardModel)
        gameModel.setPiecesInGame(s)
        def nC = new PawnsStandardMoveRule(gameModel.getPiecesInGame())

        when:
        Set<Move> r = piece.getMoves(gameModel.getBoardModel())
        nC.obyRule(r, piece)
        def ls = r.size()

        then:
        ls == 0
    }
}
