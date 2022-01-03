package model.game.rules

import model.game.GameModel
import model.game.Position
import model.game.board.BoardModel
import model.game.board.SquareBoard
import model.game.move.Move
import model.game.pieces.King
import model.game.pieces.Piece
import model.game.pieces.Queen
import model.game.pieces.Rook
import spock.lang.Specification

class CastleTest extends Specification {
    GameModel gameModel
    Piece piece
    Rook rook

    def setup() {
        Set<Piece> pieces = new HashSet<>()
        Set<Rule> rules = new HashSet<>()
        gameModel = new GameModel()
        gameModel.setBoardModel(new SquareBoard(8))

        rules.add(new Castle(gameModel))

        'a king in e8 with a rook in h8 - castle short'
        Piece king = new King(Piece.COLOR.BLACK, new Position(1,5))
        rook = new Rook(Piece.COLOR.BLACK, new Position(1,8))
        pieces.add(king)
        pieces.add(rook)

        piece = Stub(King)
        piece.getMovesPositions(_ as BoardModel) >> new HashSet<Position>()
        piece.getPosition() >> new Position(1,5)
        piece.getColor() >> Piece.COLOR.BLACK

        gameModel.setPiecesInGame(pieces)
    }

    def "Castling when king is not under threat"() {
        given:
        def castleFilter = new Castle(gameModel)
        when:
        Set<Move> legalMoves = piece.getMoves(gameModel.getBoardModel())
        castleFilter.obyRule(legalMoves, piece)
        legalMoves[0].execute()
        then:
        legalMoves.size() == 1
        'when castling the pos of the king would be g8 and rook new pos f8'
        // maybe make a Move class to implement this behaviour ... or extend position(it seems better) ?
        rook.getPosition() == new Position(1,6)
    }

    def "Not Castling when king is in threat"() {
        given:
        Set<Piece> pieces = gameModel.getPiecesInGame()
        pieces.add(new Queen(Piece.COLOR.White, new Position(3,5)))
        gameModel.setPiecesInGame(pieces) // just to readability
        def castleFilter = new Castle(gameModel)
        def k = (King) piece
        'The responsibility to set the king in check should be of the game'
        k.inCheck() >> true
        when:
        Set<Move> legalMoves = piece.getMoves(gameModel.getBoardModel())
        castleFilter.obyRule(legalMoves, piece)
        then: 'No legal moves to king'
        legalMoves.size() == 0
    }

    def "Not Castling after one of the pieces as moved"() { // TODO: VERIFY IF THE TEST IS CORRECTED
        given:
        def aPiece= gameModel.getPiecesInGame()[0]
        def pos = aPiece.getPosition()
        aPiece.moveToPosition(new Position(0,0))
        aPiece.moveToPosition(pos)
        def castleFilter = new Castle(gameModel)
        piece.isMoved() >> true
        when:
        Set<Move> legalMoves = piece.getMoves(gameModel.getBoardModel())
        castleFilter.obyRule(legalMoves, piece)
        then: 'No legal moves to king'
        legalMoves.size() == 0

    }
}
