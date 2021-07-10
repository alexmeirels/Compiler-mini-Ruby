package lexical;

import java.io.FileInputStream;
import java.io.PushbackInputStream;

public class LexicalAnalysis implements AutoCloseable {

    private int line;
    private SymbolTable st;
    private PushbackInputStream input;

    public LexicalAnalysis(String filename) throws LexicalException {
        try {
            input = new PushbackInputStream(new FileInputStream(filename));
        } catch (Exception e) {
            throw new LexicalException("Unable to open file");
        }

        st = new SymbolTable();
        line = 1;
    }

    public void close() throws LexicalException {
        try {
            input.close();
        } catch (Exception e) {
            throw new LexicalException("Unable to close file");
        }
    }

    public int getLine() {
        return this.line;
    }

    public Lexeme nextToken() {
        Lexeme lex = new Lexeme("", TokenType.END_OF_FILE);

        int state = 1;
        while (state != 12 && state != 13) {
            int c = getc();

            switch (state) {
                case 1:
                    if (c == ' ' || c == '\t' || c == '\r') {
                        state = 1;
                    } else if (c == '\n') {
                        line++;
                        state = 1;
                    } else if (c == '#') {
                        state = 2;
                    } else if (c == '.') {
                        lex.token += (char) c;
                        state = 3;
                    } else if (c == '=') {
                        lex.token += (char) c;
                        state = 5;
                    } else if (c == '<' || c == '>') {
                        lex.token += (char) c;
                        state = 6;
                    } else if (c == '*') {
                        lex.token += (char) c;
                        state = 7;
                    } else if (c == '!') {
                        lex.token += (char) c;
                        state = 8;
                    }

                    else if (c == '_' || Character.isLetter(c)) {
                        lex.token += (char) c;
                        state = 9;
                    } else if (Character.isDigit(c)) {
                        lex.token += (char) c;
                        state = 10;
                    } else if (c == '\'') {
                        state = 11;
                    } else if (c == ';' || c == ',' || c == '+' || c == '-' || c == '%' || c == '/' || c == '['
                            || c == ']' || c == '(' || c == ')') {
                        lex.token += (char) c;
                        state = 12;
                    } else if (c == -1) {
                        lex.type = TokenType.END_OF_FILE;
                        state = 13;
                    } else {
                        lex.token += (char) c;
                        lex.type = TokenType.INVALID_TOKEN;
                        state = 13;
                    }
                    break;

                case 2:
                    if (c == '\n') {
                        line++;
                        state = 1;
                    } else if (c == -1) {
                        lex.type = TokenType.END_OF_FILE;
                        state = 13;
                    } else {
                        state = 2;
                    }
                    break;

                case 3:
                    if (c == '.') {
                        lex.token += (char) c;
                        state = 4;
                    } else {
                        if (c != -1)
                            ungetc(c);

                        state = 12;
                    }
                    break;

                case 4:
                    if (c == '.') {
                        lex.token += (char) c;
                        state = 12;
                    } else {
                        if (c != -1)
                            ungetc(c);

                        state = 12;
                    }
                    break;

                case 5:
                    if (c == '=') {
                        lex.token += (char) c;
                        state = 6;
                    } else {
                        if (c != -1)
                            ungetc(c);

                        state = 12;
                    }
                    break;

                case 6:
                    if (c == '=') {
                        lex.token += (char) c;
                        state = 12;
                    } else {
                        if (c != -1)
                            ungetc(c);

                        state = 12;
                    }
                    break;

                case 7:
                    if (c == '*') {
                        lex.token += (char) c;
                        state = 12;
                    } else {
                        if (c != -1)
                            ungetc(c);

                        state = 12;
                    }
                    break;

                case 8:
                    if (c == '=') {
                        lex.token += (char) c;
                        state = 12;
                    } else {
                        if (c == -1) {
                            lex.type = TokenType.UNEXPECTED_EOF;
                            state = 13;
                        }
                    }
                    break;

                case 9:
                    if (c == '_' || Character.isLetter(c) || Character.isDigit(c)) {
                        lex.token += (char) c;
                        state = 9;
                    } else {
                        if (c != -1)
                            ungetc(c);

                        state = 12;
                    }
                    break;

                case 10:
                    if (Character.isDigit(c)) {
                        lex.token += (char) c;
                        state = 10;
                    } else {
                        if (c != -1)
                            ungetc(c);

                        lex.type = TokenType.INTEGER;
                        state = 13;
                    }
                    break;

                case 11:
                    if (c != '\'') {
                        if (c == ';') {
                            lex.type = TokenType.UNEXPECTED_EOF;
                            state = 13;
                        } else {
                            lex.token += (char) c;
                            state = 11;
                        }
                    } else {
                        if (c == -1) {
                            // ungetc(c, m_file);
                            lex.type = TokenType.UNEXPECTED_EOF;
                            state = 13;
                        } else {
                            // lex.token += (char)c;
                            lex.type = TokenType.STRING;
                            state = 13;
                        }
                    }
                    break;

                default:
                    assert (false);
            }
        }

        if (state == 12)
            lex.type = st.find(lex.token);

        return lex;
    }

    private int getc() {
        try {
            return input.read();
        } catch (Exception e) {
            throw new LexicalException("Unable to read file");
        }
    }

    private void ungetc(int c) {
        if (c != -1) {
            try {
                input.unread(c);
            } catch (Exception e) {
                throw new LexicalException("Unable to ungetc");
            }
        }
    }
}
