package org.github.madbrain.demo.rulez.api.parser;

public class RuleLexer {
    private final String content;
    private int position = 0;

    public RuleLexer(String content) {
        this.content = content;
    }

    public Token nextToken() {
        while (true) {
            var c = getChar();
            if (c == 0) {
                return new Token(TokenType.EOF, null);
            }
            if (c == '$') {
                return variable();
            }
            if (isLetter(c)) {
                return identifier(c);
            }
            if (isDigit(c)) {
                return integer(c);
            }
            if (c == ':') {
                return new Token(TokenType.COLON, null);
            }
            if (c == '(') {
                return new Token(TokenType.LPAR, null);
            }
            if (c == ')') {
                return new Token(TokenType.RPAR, null);
            }
            if (c == '>') {
                var cc = getChar();
                if (cc == '=') {
                    return new Token(TokenType.GE, null);
                }
                ungetChar(cc);
                return new Token(TokenType.GT, null);
            }
            if (c == '=') {
                var cc = getChar();
                if (cc == '=') {
                    return new Token(TokenType.EQ, null);
                }
                ungetChar(cc);
            }
            if (!isSpace(c)) {
                throw new RuntimeException("Unexpected character '" + c + "'");
            }
        }
    }

    private Token integer(char cc) {
        var builder = new StringBuilder();
        builder.append(cc);
        while (true) {
            var c = getChar();
            if (!isDigit(c)) {
                ungetChar(c);
                break;
            }
            builder.append(c);
        }
        return new Token(TokenType.INTEGER, builder.toString());
    }

    private Token identifier(char cc) {
        var builder = new StringBuilder();
        builder.append(cc);
        while (true) {
            var c = getChar();
            if (!isLetterOrDigit(c)) {
                ungetChar(c);
                break;
            }
            builder.append(c);
        }
        return new Token(TokenType.IDENTIFIER, builder.toString());
    }

    private Token variable() {
        var builder = new StringBuilder();
        while (true) {
            var c = getChar();
            if (!isLetterOrDigit(c)) {
                ungetChar(c);
                break;
            }
            builder.append(c);
        }
        return new Token(TokenType.VARIABLE, builder.toString());
    }

    private static boolean isLetterOrDigit(char c) {
        return isLetter(c) || isDigit(c);
    }

    private static boolean isLetter(char c) {
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isSpace(char c) {
        return c == ' ' || c == '\n' || c == '\t';
    }

    private char getChar() {
        if (position >= content.length()) {
            return 0;
        }
        return content.charAt(position++);
    }

    private void ungetChar(char c) {
        if (c > 0) {
            --position;
        }
    }
}
