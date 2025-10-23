import io.github.airbag.Airbag;
import io.github.airbag.expression.ExpressionParser;
import io.github.airbag.symbol.Symbol;
import io.github.airbag.symbol.SymbolFormatter;
import io.github.airbag.symbol.SymbolProvider;
import io.github.airbag.tree.DerivationTree;
import io.github.airbag.tree.TreeFormatter;
import io.github.airbag.tree.TreeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ExpressionTest {

    private Airbag airbag;
    private SymbolProvider symbolProvider;
    private TreeProvider treeProvider;

    @BeforeEach
    void setup() {
        airbag = Airbag.testGrammar("io.github.airbag.expression.Expression");
        symbolProvider = airbag.getSymbolProvider();
        treeProvider = airbag.getTreeProvider();
    }

    @Test
    void testLexer() {
        //Create symbols/token from specification.
        List<Symbol> expected = symbolProvider.fromSpec("(ID 'x') '=' (INT '5') (NEWLINE '\\n') EOF");

        //Create symbols/token from input.
        List<Symbol> actual = symbolProvider.fromInput("x = 5\n");

        //Assert lists.
        airbag.assertSymbolList(expected, actual);
    }

    @Test
    void testLexerFullyDetail() {
        //Switch the symbol formatter for something with more detail.
        symbolProvider.setFormatter(SymbolFormatter.ANTLR);

        //Create symbols with full detail.
        List<Symbol> expected = symbolProvider.fromSpec("""
                [@0,0:0='x',<ID>,1:0]
                [@1,2:2='=',<'='>,1:2]
                [@2,4:4='5',<INT>,1:4]
                [@3,5:5='\\n',<NEWLINE>,1:5]
                [@4,6:5='<EOF>',<EOF>,2:0]
                """);

        //Create symbols/token from input.
        List<Symbol> actual = symbolProvider.fromInput("x = 5\n");

        //Assert lists.
        airbag.assertSymbolList(expected, actual);
    }

    @Test
    void testParseTree() {
        //Create symbols/token from specification.
        List<Symbol> expected = symbolProvider.fromSpec(
                "(ID 'x') '*' '(' (ID 'y') '+' (INT '5') ')' EOF");

        //Create symbols/token from input.
        List<Symbol> actual = symbolProvider.fromInput("x * (y + 5)");

        //Assert lists.
        airbag.assertSymbolList(expected, actual);

        //Build expected tree
        DerivationTree expectedTree = treeProvider.fromSpec(
                """
                 (expr
                     (expr (ID 'x'))
                     '*'
                     (expr
                         '('
                             (expr
                                 (expr (ID  'y'))
                                 '+'
                                 (expr (INT '5'))
                             )
                         ')'
                     )
                 )""");

        //Actual tree
        DerivationTree actualTree = treeProvider.fromInput(expected, "expr");

        //Assert tree
        airbag.assertTree(expectedTree, actualTree);
    }

}