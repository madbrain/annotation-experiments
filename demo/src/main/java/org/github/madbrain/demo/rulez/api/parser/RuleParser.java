package org.github.madbrain.demo.rulez.api.parser;

import java.util.ArrayList;

public class RuleParser {

    private final RuleLexer lexer;
    private Token token;

    public RuleParser(String spec) {
        lexer = new RuleLexer(spec);
        this.nextToken();
    }

    public Spec parse() {
        var matches = new ArrayList<ObjectMatch>();
        while (token.type() != TokenType.EOF) {
            matches.add(parseStatement());
        }
        return new Spec(matches);
    }

    private ObjectMatch parseStatement() {
        String varName = null;
        if (token.type() == TokenType.VARIABLE) {
            varName = token.value();
            nextToken();
            expect(TokenType.COLON);
        }
        var typeName = expectIdentifier();
        expect(TokenType.LPAR);
        var conditions = new ArrayList<>();
        while (token.type() != TokenType.RPAR) {
            var condition = parseCondition();
            conditions.add(condition);
        }
        expect(TokenType.RPAR);
        return new ObjectMatch(varName, typeName, conditions);
    }

    private Object parseCondition() {
        var left = parseAtom();
        if (token.type() == TokenType.GT) {
            nextToken();
            var right = parseAtom();
            return new OperationCall(left, Operation.GT, right);
        }
        if (token.type() == TokenType.GE) {
            nextToken();
            var right = parseAtom();
            return new OperationCall(left, Operation.GE, right);
        }
        if (token.type() == TokenType.EQ) {
            nextToken();
            var right = parseAtom();
            return new OperationCall(left, Operation.EQ, right);
        }
        throw new RuntimeException("expecting operator, got " + token.type());
    }

    private Expression parseAtom() {
        if (token.type() == TokenType.IDENTIFIER) {
            var name = token.value();
            nextToken();
            return new Identifier(name);
        }
        if (token.type() == TokenType.INTEGER) {
            var value = Integer.parseInt(token.value());
            nextToken();
            return new IntegerLiteral(value);
        }
        throw new RuntimeException("expecting IDENTIFIER or INTEGER as atom, got " + token.type());
    }

    private String expectIdentifier() {
        var t = expect(TokenType.IDENTIFIER);
        return t.value();
    }

    private Token expect(TokenType tokenType) {
        if (this.token.type() != tokenType) {
            throw new RuntimeException("expecting token " + tokenType + ", got " + this.token.type());
        }
        var t = this.token;
        this.nextToken();
        return t;
    }

    private Token nextToken() {
        this.token = lexer.nextToken();
        return this.token;
    }
}
