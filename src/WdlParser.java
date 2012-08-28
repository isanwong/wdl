
import java.util.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Arrays;
class WdlParser implements Parser {
  private TokenStream tokens;
  private HashMap<String, ExpressionParser> expressionParsers;
  private SyntaxErrorFormatter syntaxErrorFormatter;
  private Map<String, TerminalIdentifier[]> first;
  private Map<String, TerminalIdentifier[]> follow;
  private Map<String, List<String>> nonterminal_rules;
  private Map<Integer, String> rules;
  /* table[nonterminal][terminal] = rule */
  private static final int[][] table = {
    { -1, -1, -1, -1, -1, 41, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 11, -1, -1, -1, -1, -1, -1, 14, 11, -1 },
    { -1, -1, -1, -1, -1, -1, 24, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, -1, -1, -1, -1, -1, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, -1, -1, -1, -1, -1, -1, 29, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, -1, -1, -1, -1, -1, 37, -1, -1, -1, -1, -1, -1, 7, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 32, -1, -1, -1, -1, -1, -1, -1, 33, -1 },
    { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 22, -1, -1 },
    { -1, -1, -1, -1, 21, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 35 },
    { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 40, -1, -1, -1, -1 },
    { -1, -1, -1, 27, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, -1, -1, -1, 4, 30, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 6, -1, -1, -1, -1 },
    { -1, -1, -1, -1, 34, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, 44, -1, -1, -1, -1, -1, -1, 44, -1, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, -1, 19, 13, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, -1, -1, -1, -1, -1, 26, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, 43, -1, -1, -1, -1, -1, -1, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, -1, -1, -1, -1, -1, 38, -1, -1, -1, -1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, -1, -1, -1, -1, -1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, -1, -1, -1, 18, 18, -1, -1, -1, 36, -1, -1, -1, -1, -1, -1, 18, -1, -1, -1, -1 },
    { -1, -1, -1, -1, -1, 42, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, -1, -1, -1, -1, -1, 31, -1, 3, -1, -1, -1, 17, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, -1, -1, -1, -1, -1, 20, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, 25, -1, -1, -1, -1, -1, -1, -1, -1, -1, 39, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, -1, -1, -1, 28, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 5, -1, -1, -1, -1, -1, -1, -1, 1, -1 },
  };
  public enum TerminalId implements TerminalIdentifier {
    TERMINAL_RPAREN(0, "rparen"),
    TERMINAL_OUTPUT(1, "output"),
    TERMINAL_FILE(2, "file"),
    TERMINAL_LSQUARE(3, "lsquare"),
    TERMINAL_AS(4, "as"),
    TERMINAL_COMPOSITE_TASK(5, "composite_task"),
    TERMINAL_STEP(6, "step"),
    TERMINAL_IDENTIFIER(7, "identifier"),
    TERMINAL_INPUT(8, "input"),
    TERMINAL_STRING(9, "string"),
    TERMINAL_RBRACE(10, "rbrace"),
    TERMINAL_IN(11, "in"),
    TERMINAL_SEMI(12, "semi"),
    TERMINAL_NUMBER(13, "number"),
    TERMINAL_RSQUARE(14, "rsquare"),
    TERMINAL_COLON(15, "colon"),
    TERMINAL_ASSIGN(16, "assign"),
    TERMINAL_FOR(17, "for"),
    TERMINAL_LPAREN(18, "lparen"),
    TERMINAL_DOT(19, "dot"),
    TERMINAL_COMMA(20, "comma"),
    TERMINAL_LBRACE(21, "lbrace");
    private final int id;
    private final String string;
    TerminalId(int id, String string) {
      this.id = id;
      this.string = string;
    }
    public int id() {return id;}
    public String string() {return string;}
  }
  private class WdlTerminalMap implements TerminalMap {
    private Map<Integer, String> id_to_str;
    private Map<String, Integer> str_to_id;
    WdlTerminalMap(TerminalId[] terminals) {
      id_to_str = new HashMap<Integer, String>();
      str_to_id = new HashMap<String, Integer>();
      for( TerminalId terminal : terminals ) {
        Integer id = new Integer(terminal.id());
        String str = terminal.string();
        id_to_str.put(id, str);
        str_to_id.put(str, id);
      }
    }
    public int get(String string) { return this.str_to_id.get(string); }
    public String get(int id) { return this.id_to_str.get(id); }
    public boolean isValid(String string) { return this.str_to_id.containsKey(string); }
    public boolean isValid(int id) { return this.id_to_str.containsKey(id); }
  }
  WdlParser(SyntaxErrorFormatter syntaxErrorFormatter) {
    this.syntaxErrorFormatter = syntaxErrorFormatter; 
    this.expressionParsers = new HashMap<String, ExpressionParser>();
    this.first = new HashMap<String, TerminalIdentifier[]>();
    this.follow = new HashMap<String, TerminalIdentifier[]>();
    this.nonterminal_rules = new HashMap<String, List<String>>();
    this.rules = new HashMap<Integer, String>();
    ArrayList<TerminalId> list;
    String rule;
    this.nonterminal_rules.put("wdl_entity", new ArrayList());
    this.nonterminal_rules.put("_gen10", new ArrayList());
    this.nonterminal_rules.put("step", new ArrayList());
    this.nonterminal_rules.put("task_attr", new ArrayList());
    this.nonterminal_rules.put("step_input_list", new ArrayList());
    this.nonterminal_rules.put("_gen5", new ArrayList());
    this.nonterminal_rules.put("_gen9", new ArrayList());
    this.nonterminal_rules.put("variable_member", new ArrayList());
    this.nonterminal_rules.put("_gen2", new ArrayList());
    this.nonterminal_rules.put("for_loop", new ArrayList());
    this.nonterminal_rules.put("task_attrs", new ArrayList());
    this.nonterminal_rules.put("composite_task_entity", new ArrayList());
    this.nonterminal_rules.put("step_name", new ArrayList());
    this.nonterminal_rules.put("_gen3", new ArrayList());
    this.nonterminal_rules.put("_gen4", new ArrayList());
    this.nonterminal_rules.put("step_output", new ArrayList());
    this.nonterminal_rules.put("task_identifier", new ArrayList());
    this.nonterminal_rules.put("step_attr", new ArrayList());
    this.nonterminal_rules.put("_gen6", new ArrayList());
    this.nonterminal_rules.put("step_input", new ArrayList());
    this.nonterminal_rules.put("_gen1", new ArrayList());
    this.nonterminal_rules.put("_gen0", new ArrayList());
    this.nonterminal_rules.put("step_output_list", new ArrayList());
    this.nonterminal_rules.put("task_attr_value", new ArrayList());
    this.nonterminal_rules.put("variable", new ArrayList());
    this.nonterminal_rules.put("_gen8", new ArrayList());
    this.nonterminal_rules.put("wdl", new ArrayList());
    this.nonterminal_rules.put("composite_task", new ArrayList());
    this.nonterminal_rules.put("_gen7", new ArrayList());
    rule = "wdl := _gen0";
    this.nonterminal_rules.get("wdl").add(rule);
    this.rules.put(new Integer(0), rule);
    rule = "_gen7 := 'comma' step_input _gen7";
    this.nonterminal_rules.get("_gen7").add(rule);
    this.rules.put(new Integer(1), rule);
    rule = "step_input := 'identifier' 'assign' variable -> StepInput( parameter=$0, value=$2 )";
    this.nonterminal_rules.get("step_input").add(rule);
    this.rules.put(new Integer(2), rule);
    rule = "task_attr_value := 'string'";
    this.nonterminal_rules.get("task_attr_value").add(rule);
    this.rules.put(new Integer(3), rule);
    rule = "composite_task_entity := composite_task";
    this.nonterminal_rules.get("composite_task_entity").add(rule);
    this.rules.put(new Integer(4), rule);
    rule = "_gen7 := ε";
    this.nonterminal_rules.get("_gen7").add(rule);
    this.rules.put(new Integer(5), rule);
    rule = "composite_task_entity := for_loop";
    this.nonterminal_rules.get("composite_task_entity").add(rule);
    this.rules.put(new Integer(6), rule);
    rule = "_gen5 := ε";
    this.nonterminal_rules.get("_gen5").add(rule);
    this.rules.put(new Integer(7), rule);
    rule = "_gen6 := ε";
    this.nonterminal_rules.get("_gen6").add(rule);
    this.rules.put(new Integer(8), rule);
    rule = "step_attr := step_input_list";
    this.nonterminal_rules.get("step_attr").add(rule);
    this.rules.put(new Integer(9), rule);
    rule = "task_attr := 'identifier' 'assign' task_attr_value -> TaskAttribute( value=$2, key=$0 )";
    this.nonterminal_rules.get("task_attr").add(rule);
    this.rules.put(new Integer(10), rule);
    rule = "_gen10 := ε";
    this.nonterminal_rules.get("_gen10").add(rule);
    this.rules.put(new Integer(11), rule);
    rule = "step_output := 'file' 'lparen' 'string' 'rparen' 'as' variable -> StepFileOutput( as=$5, file=$2 )";
    this.nonterminal_rules.get("step_output").add(rule);
    this.rules.put(new Integer(12), rule);
    rule = "_gen4 := ε";
    this.nonterminal_rules.get("_gen4").add(rule);
    this.rules.put(new Integer(13), rule);
    rule = "_gen10 := variable_member";
    this.nonterminal_rules.get("_gen10").add(rule);
    this.rules.put(new Integer(14), rule);
    rule = "step_output_list := 'output' 'colon' _gen8 'semi' -> StepOutputList( outputs=$2 )";
    this.nonterminal_rules.get("step_output_list").add(rule);
    this.rules.put(new Integer(15), rule);
    rule = "_gen3 := ε";
    this.nonterminal_rules.get("_gen3").add(rule);
    this.rules.put(new Integer(16), rule);
    rule = "task_attr_value := 'number'";
    this.nonterminal_rules.get("task_attr_value").add(rule);
    this.rules.put(new Integer(17), rule);
    rule = "_gen1 := composite_task_entity _gen1";
    this.nonterminal_rules.get("_gen1").add(rule);
    this.rules.put(new Integer(18), rule);
    rule = "_gen4 := task_attrs";
    this.nonterminal_rules.get("_gen4").add(rule);
    this.rules.put(new Integer(19), rule);
    rule = "variable := 'identifier' _gen10 -> Variable( member=$1, name=$0 )";
    this.nonterminal_rules.get("variable").add(rule);
    this.rules.put(new Integer(20), rule);
    rule = "_gen2 := step_name";
    this.nonterminal_rules.get("_gen2").add(rule);
    this.rules.put(new Integer(21), rule);
    rule = "variable_member := 'dot' 'identifier' -> $1";
    this.nonterminal_rules.get("variable_member").add(rule);
    this.rules.put(new Integer(22), rule);
    rule = "_gen0 := ε";
    this.nonterminal_rules.get("_gen0").add(rule);
    this.rules.put(new Integer(23), rule);
    rule = "step := 'step' task_identifier _gen2 'lbrace' _gen3 'rbrace' -> Step( body=$4, task=$1, name=$2 )";
    this.nonterminal_rules.get("step").add(rule);
    this.rules.put(new Integer(24), rule);
    rule = "_gen8 := step_output _gen9";
    this.nonterminal_rules.get("_gen8").add(rule);
    this.rules.put(new Integer(25), rule);
    rule = "task_identifier := 'identifier' _gen4 -> Task( attributes=$1, name=$0 )";
    this.nonterminal_rules.get("task_identifier").add(rule);
    this.rules.put(new Integer(26), rule);
    rule = "task_attrs := 'lsquare' _gen5 'rsquare' -> $1";
    this.nonterminal_rules.get("task_attrs").add(rule);
    this.rules.put(new Integer(27), rule);
    rule = "composite_task := 'composite_task' 'identifier' 'lbrace' _gen1 'rbrace' -> CompositeTask( body=$3, name=$1 )";
    this.nonterminal_rules.get("composite_task").add(rule);
    this.rules.put(new Integer(28), rule);
    rule = "step_input_list := 'input' 'colon' _gen6 'semi' -> StepInputList( inputs=$2 )";
    this.nonterminal_rules.get("step_input_list").add(rule);
    this.rules.put(new Integer(29), rule);
    rule = "composite_task_entity := step";
    this.nonterminal_rules.get("composite_task_entity").add(rule);
    this.rules.put(new Integer(30), rule);
    rule = "task_attr_value := 'identifier'";
    this.nonterminal_rules.get("task_attr_value").add(rule);
    this.rules.put(new Integer(31), rule);
    rule = "_gen9 := ε";
    this.nonterminal_rules.get("_gen9").add(rule);
    this.rules.put(new Integer(32), rule);
    rule = "_gen9 := 'comma' step_output _gen9";
    this.nonterminal_rules.get("_gen9").add(rule);
    this.rules.put(new Integer(33), rule);
    rule = "step_name := 'as' 'identifier' -> $1";
    this.nonterminal_rules.get("step_name").add(rule);
    this.rules.put(new Integer(34), rule);
    rule = "_gen2 := ε";
    this.nonterminal_rules.get("_gen2").add(rule);
    this.rules.put(new Integer(35), rule);
    rule = "_gen1 := ε";
    this.nonterminal_rules.get("_gen1").add(rule);
    this.rules.put(new Integer(36), rule);
    rule = "_gen5 := task_attr _gen5";
    this.nonterminal_rules.get("_gen5").add(rule);
    this.rules.put(new Integer(37), rule);
    rule = "_gen6 := step_input _gen7";
    this.nonterminal_rules.get("_gen6").add(rule);
    this.rules.put(new Integer(38), rule);
    rule = "_gen8 := ε";
    this.nonterminal_rules.get("_gen8").add(rule);
    this.rules.put(new Integer(39), rule);
    rule = "for_loop := 'for' 'lparen' 'identifier' 'in' 'identifier' 'rparen' 'lbrace' _gen1 'rbrace' -> ForLoop( body=$7, item=$2, collection=$4 )";
    this.nonterminal_rules.get("for_loop").add(rule);
    this.rules.put(new Integer(40), rule);
    rule = "wdl_entity := composite_task";
    this.nonterminal_rules.get("wdl_entity").add(rule);
    this.rules.put(new Integer(41), rule);
    rule = "_gen0 := wdl_entity _gen0";
    this.nonterminal_rules.get("_gen0").add(rule);
    this.rules.put(new Integer(42), rule);
    rule = "step_attr := step_output_list";
    this.nonterminal_rules.get("step_attr").add(rule);
    this.rules.put(new Integer(43), rule);
    rule = "_gen3 := step_attr _gen3";
    this.nonterminal_rules.get("_gen3").add(rule);
    this.rules.put(new Integer(44), rule);
    this.first.put("wdl_entity", new TerminalId[] { TerminalId.TERMINAL_COMPOSITE_TASK });
    this.first.put("_gen10", new TerminalId[] { TerminalId.TERMINAL_DOT });
    this.first.put("step", new TerminalId[] { TerminalId.TERMINAL_STEP });
    this.first.put("task_attr", new TerminalId[] { TerminalId.TERMINAL_IDENTIFIER });
    this.first.put("step_input_list", new TerminalId[] { TerminalId.TERMINAL_INPUT });
    this.first.put("_gen5", new TerminalId[] { TerminalId.TERMINAL_IDENTIFIER });
    this.first.put("_gen9", new TerminalId[] { TerminalId.TERMINAL_COMMA });
    this.first.put("variable_member", new TerminalId[] { TerminalId.TERMINAL_DOT });
    this.first.put("_gen2", new TerminalId[] { TerminalId.TERMINAL_AS });
    this.first.put("for_loop", new TerminalId[] { TerminalId.TERMINAL_FOR });
    this.first.put("task_attrs", new TerminalId[] { TerminalId.TERMINAL_LSQUARE });
    this.first.put("composite_task_entity", new TerminalId[] { TerminalId.TERMINAL_FOR, TerminalId.TERMINAL_COMPOSITE_TASK, TerminalId.TERMINAL_STEP });
    this.first.put("step_name", new TerminalId[] { TerminalId.TERMINAL_AS });
    this.first.put("_gen3", new TerminalId[] { TerminalId.TERMINAL_INPUT, TerminalId.TERMINAL_OUTPUT });
    this.first.put("_gen4", new TerminalId[] { TerminalId.TERMINAL_LSQUARE });
    this.first.put("step_output", new TerminalId[] { TerminalId.TERMINAL_FILE });
    this.first.put("task_identifier", new TerminalId[] { TerminalId.TERMINAL_IDENTIFIER });
    this.first.put("step_attr", new TerminalId[] { TerminalId.TERMINAL_INPUT, TerminalId.TERMINAL_OUTPUT });
    this.first.put("_gen6", new TerminalId[] { TerminalId.TERMINAL_IDENTIFIER });
    this.first.put("step_input", new TerminalId[] { TerminalId.TERMINAL_IDENTIFIER });
    this.first.put("_gen1", new TerminalId[] { TerminalId.TERMINAL_FOR, TerminalId.TERMINAL_COMPOSITE_TASK, TerminalId.TERMINAL_STEP });
    this.first.put("_gen0", new TerminalId[] { TerminalId.TERMINAL_COMPOSITE_TASK });
    this.first.put("step_output_list", new TerminalId[] { TerminalId.TERMINAL_OUTPUT });
    this.first.put("task_attr_value", new TerminalId[] { TerminalId.TERMINAL_IDENTIFIER, TerminalId.TERMINAL_NUMBER, TerminalId.TERMINAL_STRING });
    this.first.put("variable", new TerminalId[] { TerminalId.TERMINAL_IDENTIFIER });
    this.first.put("_gen8", new TerminalId[] { TerminalId.TERMINAL_FILE });
    this.first.put("wdl", new TerminalId[] { TerminalId.TERMINAL_COMPOSITE_TASK });
    this.first.put("composite_task", new TerminalId[] { TerminalId.TERMINAL_COMPOSITE_TASK });
    this.first.put("_gen7", new TerminalId[] { TerminalId.TERMINAL_COMMA });
    this.follow.put("wdl_entity", new TerminalId[] { TerminalId.TERMINAL_COMPOSITE_TASK });
    this.follow.put("_gen10", new TerminalId[] { TerminalId.TERMINAL_SEMI, TerminalId.TERMINAL_COMMA });
    this.follow.put("step", new TerminalId[] { TerminalId.TERMINAL_FOR, TerminalId.TERMINAL_COMPOSITE_TASK, TerminalId.TERMINAL_STEP, TerminalId.TERMINAL_RBRACE });
    this.follow.put("task_attr", new TerminalId[] { TerminalId.TERMINAL_IDENTIFIER, TerminalId.TERMINAL_RSQUARE });
    this.follow.put("step_input_list", new TerminalId[] { TerminalId.TERMINAL_RBRACE, TerminalId.TERMINAL_INPUT, TerminalId.TERMINAL_OUTPUT });
    this.follow.put("_gen5", new TerminalId[] { TerminalId.TERMINAL_RSQUARE });
    this.follow.put("_gen9", new TerminalId[] { TerminalId.TERMINAL_SEMI });
    this.follow.put("variable_member", new TerminalId[] { TerminalId.TERMINAL_SEMI, TerminalId.TERMINAL_COMMA });
    this.follow.put("_gen2", new TerminalId[] { TerminalId.TERMINAL_LBRACE });
    this.follow.put("for_loop", new TerminalId[] { TerminalId.TERMINAL_FOR, TerminalId.TERMINAL_COMPOSITE_TASK, TerminalId.TERMINAL_STEP, TerminalId.TERMINAL_RBRACE });
    this.follow.put("task_attrs", new TerminalId[] { TerminalId.TERMINAL_AS });
    this.follow.put("composite_task_entity", new TerminalId[] { TerminalId.TERMINAL_FOR, TerminalId.TERMINAL_COMPOSITE_TASK, TerminalId.TERMINAL_STEP, TerminalId.TERMINAL_RBRACE });
    this.follow.put("step_name", new TerminalId[] { TerminalId.TERMINAL_LBRACE });
    this.follow.put("_gen3", new TerminalId[] { TerminalId.TERMINAL_RBRACE });
    this.follow.put("_gen4", new TerminalId[] { TerminalId.TERMINAL_AS });
    this.follow.put("step_output", new TerminalId[] { TerminalId.TERMINAL_SEMI, TerminalId.TERMINAL_COMMA });
    this.follow.put("task_identifier", new TerminalId[] { TerminalId.TERMINAL_AS });
    this.follow.put("step_attr", new TerminalId[] { TerminalId.TERMINAL_RBRACE, TerminalId.TERMINAL_INPUT, TerminalId.TERMINAL_OUTPUT });
    this.follow.put("_gen6", new TerminalId[] { TerminalId.TERMINAL_SEMI });
    this.follow.put("step_input", new TerminalId[] { TerminalId.TERMINAL_SEMI, TerminalId.TERMINAL_COMMA });
    this.follow.put("_gen1", new TerminalId[] { TerminalId.TERMINAL_RBRACE });
    this.follow.put("_gen0", new TerminalId[] {  });
    this.follow.put("step_output_list", new TerminalId[] { TerminalId.TERMINAL_RBRACE, TerminalId.TERMINAL_INPUT, TerminalId.TERMINAL_OUTPUT });
    this.follow.put("task_attr_value", new TerminalId[] { TerminalId.TERMINAL_IDENTIFIER, TerminalId.TERMINAL_RSQUARE });
    this.follow.put("variable", new TerminalId[] { TerminalId.TERMINAL_SEMI, TerminalId.TERMINAL_COMMA });
    this.follow.put("_gen8", new TerminalId[] { TerminalId.TERMINAL_SEMI });
    this.follow.put("wdl", new TerminalId[] {  });
    this.follow.put("composite_task", new TerminalId[] { TerminalId.TERMINAL_FOR, TerminalId.TERMINAL_COMPOSITE_TASK, TerminalId.TERMINAL_STEP, TerminalId.TERMINAL_RBRACE });
    this.follow.put("_gen7", new TerminalId[] { TerminalId.TERMINAL_SEMI });
  }
  public TerminalMap getTerminalMap() {
    return new WdlTerminalMap(TerminalId.values());
  }
  public ParseTree parse(TokenStream tokens) throws SyntaxError {
    this.tokens = tokens;
    this.tokens.setSyntaxErrorFormatter(this.syntaxErrorFormatter);
    this.tokens.setTerminalMap(this.getTerminalMap());
    ParseTree tree = this.parse_wdl();
    if (this.tokens.current() != null) {
      StackTraceElement[] stack = Thread.currentThread().getStackTrace();
      throw new SyntaxError(this.syntaxErrorFormatter.excess_tokens(stack[1].getMethodName(), this.tokens.current()));
    }
    return tree;
  }
  private boolean isTerminal(TerminalId terminal) {
    return (0 <= terminal.id() && terminal.id() <= 21);
  }
  private boolean isNonTerminal(TerminalId terminal) {
    return (22 <= terminal.id() && terminal.id() <= 50);
  }
  private boolean isTerminal(int terminal) {
    return (0 <= terminal && terminal <= 21);
  }
  private boolean isNonTerminal(int terminal) {
    return (22 <= terminal && terminal <= 50);
  }
  private ParseTree parse_wdl_entity() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[0][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(22, "wdl_entity"));
    tree.setList(null);
    if (current == null) {
      throw new SyntaxError(this.syntaxErrorFormatter.unexpected_eof(
        "wdl_entity",
        Arrays.asList(this.first.get("wdl_entity")),
        this.nonterminal_rules.get("wdl_entity")
      ));
    }
    if (rule == 41) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      subtree = this.parse_composite_task();
      tree.add( subtree);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("wdl_entity"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "wdl_entity",
      current, 
      Arrays.asList(this.first.get("wdl_entity")),
      this.rules.get(41)
    ));
  }
  private ParseTree parse__gen10() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[1][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(23, "_gen10"));
    tree.setList(null);
    if ( current != null ) {
      if (current.getId() == 12 || current.getId() == 20) {
        return tree;
      }
    }
    if (current == null) {
      return tree;
    }
    if (rule == 14) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      subtree = this.parse_variable_member();
      tree.add( subtree);
      return tree;
    }
    return tree;
  }
  private ParseTree parse_step() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[2][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(24, "step"));
    tree.setList(null);
    if (current == null) {
      throw new SyntaxError(this.syntaxErrorFormatter.unexpected_eof(
        "step",
        Arrays.asList(this.first.get("step")),
        this.nonterminal_rules.get("step")
      ));
    }
    if (rule == 24) {
      LinkedHashMap<String, Integer> parameters = new LinkedHashMap<String, Integer>();
      parameters.put("body", 4);
      parameters.put("task", 1);
      parameters.put("name", 2);
      tree.setAstTransformation(new AstTransformNodeCreator("Step", parameters));
      next = this.tokens.expect(TerminalId.TERMINAL_STEP, "step", this.rules.get(24));
      tree.add(next);
      subtree = this.parse_task_identifier();
      tree.add( subtree);
      subtree = this.parse__gen2();
      tree.add( subtree);
      next = this.tokens.expect(TerminalId.TERMINAL_LBRACE, "step", this.rules.get(24));
      tree.add(next);
      subtree = this.parse__gen3();
      tree.add( subtree);
      next = this.tokens.expect(TerminalId.TERMINAL_RBRACE, "step", this.rules.get(24));
      tree.add(next);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("step"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "step",
      current, 
      Arrays.asList(this.first.get("step")),
      this.rules.get(24)
    ));
  }
  private ParseTree parse_task_attr() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[3][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(25, "task_attr"));
    tree.setList(null);
    if (current == null) {
      throw new SyntaxError(this.syntaxErrorFormatter.unexpected_eof(
        "task_attr",
        Arrays.asList(this.first.get("task_attr")),
        this.nonterminal_rules.get("task_attr")
      ));
    }
    if (rule == 10) {
      LinkedHashMap<String, Integer> parameters = new LinkedHashMap<String, Integer>();
      parameters.put("value", 2);
      parameters.put("key", 0);
      tree.setAstTransformation(new AstTransformNodeCreator("TaskAttribute", parameters));
      next = this.tokens.expect(TerminalId.TERMINAL_IDENTIFIER, "task_attr", this.rules.get(10));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_ASSIGN, "task_attr", this.rules.get(10));
      tree.add(next);
      subtree = this.parse_task_attr_value();
      tree.add( subtree);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("task_attr"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "task_attr",
      current, 
      Arrays.asList(this.first.get("task_attr")),
      this.rules.get(10)
    ));
  }
  private ParseTree parse_step_input_list() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[4][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(26, "step_input_list"));
    tree.setList(null);
    if (current == null) {
      throw new SyntaxError(this.syntaxErrorFormatter.unexpected_eof(
        "step_input_list",
        Arrays.asList(this.first.get("step_input_list")),
        this.nonterminal_rules.get("step_input_list")
      ));
    }
    if (rule == 29) {
      LinkedHashMap<String, Integer> parameters = new LinkedHashMap<String, Integer>();
      parameters.put("inputs", 2);
      tree.setAstTransformation(new AstTransformNodeCreator("StepInputList", parameters));
      next = this.tokens.expect(TerminalId.TERMINAL_INPUT, "step_input_list", this.rules.get(29));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_COLON, "step_input_list", this.rules.get(29));
      tree.add(next);
      subtree = this.parse__gen6();
      tree.add( subtree);
      next = this.tokens.expect(TerminalId.TERMINAL_SEMI, "step_input_list", this.rules.get(29));
      tree.add(next);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("step_input_list"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "step_input_list",
      current, 
      Arrays.asList(this.first.get("step_input_list")),
      this.rules.get(29)
    ));
  }
  private ParseTree parse__gen5() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[5][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(27, "_gen5"));
    tree.setList("nlist");
    if ( current != null ) {
      if (current.getId() == 14) {
        return tree;
      }
    }
    if (current == null) {
      return tree;
    }
    if (rule == 37) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      subtree = this.parse_task_attr();
      tree.add( subtree);
      subtree = this.parse__gen5();
      tree.add( subtree);
      return tree;
    }
    return tree;
  }
  private ParseTree parse__gen9() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[6][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(28, "_gen9"));
    tree.setList("slist");
    if ( current != null ) {
      if (current.getId() == 12) {
        return tree;
      }
    }
    if (current == null) {
      return tree;
    }
    if (rule == 33) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      next = this.tokens.expect(TerminalId.TERMINAL_COMMA, "_gen9", this.rules.get(33));
      tree.add(next);
      tree.setListSeparator(next);
      subtree = this.parse_step_output();
      tree.add( subtree);
      subtree = this.parse__gen9();
      tree.add( subtree);
      return tree;
    }
    return tree;
  }
  private ParseTree parse_variable_member() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[7][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(29, "variable_member"));
    tree.setList(null);
    if (current == null) {
      throw new SyntaxError(this.syntaxErrorFormatter.unexpected_eof(
        "variable_member",
        Arrays.asList(this.first.get("variable_member")),
        this.nonterminal_rules.get("variable_member")
      ));
    }
    if (rule == 22) {
      tree.setAstTransformation(new AstTransformSubstitution(1));
      next = this.tokens.expect(TerminalId.TERMINAL_DOT, "variable_member", this.rules.get(22));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_IDENTIFIER, "variable_member", this.rules.get(22));
      tree.add(next);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("variable_member"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "variable_member",
      current, 
      Arrays.asList(this.first.get("variable_member")),
      this.rules.get(22)
    ));
  }
  private ParseTree parse__gen2() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[8][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(30, "_gen2"));
    tree.setList(null);
    if ( current != null ) {
      if (current.getId() == 21) {
        return tree;
      }
    }
    if (current == null) {
      return tree;
    }
    if (rule == 21) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      subtree = this.parse_step_name();
      tree.add( subtree);
      return tree;
    }
    return tree;
  }
  private ParseTree parse_for_loop() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[9][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(31, "for_loop"));
    tree.setList(null);
    if (current == null) {
      throw new SyntaxError(this.syntaxErrorFormatter.unexpected_eof(
        "for_loop",
        Arrays.asList(this.first.get("for_loop")),
        this.nonterminal_rules.get("for_loop")
      ));
    }
    if (rule == 40) {
      LinkedHashMap<String, Integer> parameters = new LinkedHashMap<String, Integer>();
      parameters.put("body", 7);
      parameters.put("item", 2);
      parameters.put("collection", 4);
      tree.setAstTransformation(new AstTransformNodeCreator("ForLoop", parameters));
      next = this.tokens.expect(TerminalId.TERMINAL_FOR, "for_loop", this.rules.get(40));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_LPAREN, "for_loop", this.rules.get(40));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_IDENTIFIER, "for_loop", this.rules.get(40));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_IN, "for_loop", this.rules.get(40));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_IDENTIFIER, "for_loop", this.rules.get(40));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_RPAREN, "for_loop", this.rules.get(40));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_LBRACE, "for_loop", this.rules.get(40));
      tree.add(next);
      subtree = this.parse__gen1();
      tree.add( subtree);
      next = this.tokens.expect(TerminalId.TERMINAL_RBRACE, "for_loop", this.rules.get(40));
      tree.add(next);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("for_loop"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "for_loop",
      current, 
      Arrays.asList(this.first.get("for_loop")),
      this.rules.get(40)
    ));
  }
  private ParseTree parse_task_attrs() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[10][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(32, "task_attrs"));
    tree.setList(null);
    if (current == null) {
      throw new SyntaxError(this.syntaxErrorFormatter.unexpected_eof(
        "task_attrs",
        Arrays.asList(this.first.get("task_attrs")),
        this.nonterminal_rules.get("task_attrs")
      ));
    }
    if (rule == 27) {
      tree.setAstTransformation(new AstTransformSubstitution(1));
      next = this.tokens.expect(TerminalId.TERMINAL_LSQUARE, "task_attrs", this.rules.get(27));
      tree.add(next);
      subtree = this.parse__gen5();
      tree.add( subtree);
      next = this.tokens.expect(TerminalId.TERMINAL_RSQUARE, "task_attrs", this.rules.get(27));
      tree.add(next);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("task_attrs"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "task_attrs",
      current, 
      Arrays.asList(this.first.get("task_attrs")),
      this.rules.get(27)
    ));
  }
  private ParseTree parse_composite_task_entity() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[11][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(33, "composite_task_entity"));
    tree.setList(null);
    if (current == null) {
      throw new SyntaxError(this.syntaxErrorFormatter.unexpected_eof(
        "composite_task_entity",
        Arrays.asList(this.first.get("composite_task_entity")),
        this.nonterminal_rules.get("composite_task_entity")
      ));
    }
    if (rule == 4) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      subtree = this.parse_composite_task();
      tree.add( subtree);
      return tree;
    }
    else if (rule == 6) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      subtree = this.parse_for_loop();
      tree.add( subtree);
      return tree;
    }
    else if (rule == 30) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      subtree = this.parse_step();
      tree.add( subtree);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("composite_task_entity"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "composite_task_entity",
      current, 
      Arrays.asList(this.first.get("composite_task_entity")),
      this.rules.get(30)
    ));
  }
  private ParseTree parse_step_name() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[12][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(34, "step_name"));
    tree.setList(null);
    if (current == null) {
      throw new SyntaxError(this.syntaxErrorFormatter.unexpected_eof(
        "step_name",
        Arrays.asList(this.first.get("step_name")),
        this.nonterminal_rules.get("step_name")
      ));
    }
    if (rule == 34) {
      tree.setAstTransformation(new AstTransformSubstitution(1));
      next = this.tokens.expect(TerminalId.TERMINAL_AS, "step_name", this.rules.get(34));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_IDENTIFIER, "step_name", this.rules.get(34));
      tree.add(next);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("step_name"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "step_name",
      current, 
      Arrays.asList(this.first.get("step_name")),
      this.rules.get(34)
    ));
  }
  private ParseTree parse__gen3() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[13][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(35, "_gen3"));
    tree.setList("nlist");
    if ( current != null ) {
      if (current.getId() == 10) {
        return tree;
      }
    }
    if (current == null) {
      return tree;
    }
    if (rule == 44) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      subtree = this.parse_step_attr();
      tree.add( subtree);
      subtree = this.parse__gen3();
      tree.add( subtree);
      return tree;
    }
    return tree;
  }
  private ParseTree parse__gen4() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[14][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(36, "_gen4"));
    tree.setList(null);
    if ( current != null ) {
      if (current.getId() == 4) {
        return tree;
      }
    }
    if (current == null) {
      return tree;
    }
    if (rule == 19) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      subtree = this.parse_task_attrs();
      tree.add( subtree);
      return tree;
    }
    return tree;
  }
  private ParseTree parse_step_output() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[15][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(37, "step_output"));
    tree.setList(null);
    if (current == null) {
      throw new SyntaxError(this.syntaxErrorFormatter.unexpected_eof(
        "step_output",
        Arrays.asList(this.first.get("step_output")),
        this.nonterminal_rules.get("step_output")
      ));
    }
    if (rule == 12) {
      LinkedHashMap<String, Integer> parameters = new LinkedHashMap<String, Integer>();
      parameters.put("as", 5);
      parameters.put("file", 2);
      tree.setAstTransformation(new AstTransformNodeCreator("StepFileOutput", parameters));
      next = this.tokens.expect(TerminalId.TERMINAL_FILE, "step_output", this.rules.get(12));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_LPAREN, "step_output", this.rules.get(12));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_STRING, "step_output", this.rules.get(12));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_RPAREN, "step_output", this.rules.get(12));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_AS, "step_output", this.rules.get(12));
      tree.add(next);
      subtree = this.parse_variable();
      tree.add( subtree);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("step_output"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "step_output",
      current, 
      Arrays.asList(this.first.get("step_output")),
      this.rules.get(12)
    ));
  }
  private ParseTree parse_task_identifier() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[16][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(38, "task_identifier"));
    tree.setList(null);
    if (current == null) {
      throw new SyntaxError(this.syntaxErrorFormatter.unexpected_eof(
        "task_identifier",
        Arrays.asList(this.first.get("task_identifier")),
        this.nonterminal_rules.get("task_identifier")
      ));
    }
    if (rule == 26) {
      LinkedHashMap<String, Integer> parameters = new LinkedHashMap<String, Integer>();
      parameters.put("attributes", 1);
      parameters.put("name", 0);
      tree.setAstTransformation(new AstTransformNodeCreator("Task", parameters));
      next = this.tokens.expect(TerminalId.TERMINAL_IDENTIFIER, "task_identifier", this.rules.get(26));
      tree.add(next);
      subtree = this.parse__gen4();
      tree.add( subtree);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("task_identifier"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "task_identifier",
      current, 
      Arrays.asList(this.first.get("task_identifier")),
      this.rules.get(26)
    ));
  }
  private ParseTree parse_step_attr() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[17][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(39, "step_attr"));
    tree.setList(null);
    if (current == null) {
      throw new SyntaxError(this.syntaxErrorFormatter.unexpected_eof(
        "step_attr",
        Arrays.asList(this.first.get("step_attr")),
        this.nonterminal_rules.get("step_attr")
      ));
    }
    if (rule == 9) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      subtree = this.parse_step_input_list();
      tree.add( subtree);
      return tree;
    }
    else if (rule == 43) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      subtree = this.parse_step_output_list();
      tree.add( subtree);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("step_attr"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "step_attr",
      current, 
      Arrays.asList(this.first.get("step_attr")),
      this.rules.get(43)
    ));
  }
  private ParseTree parse__gen6() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[18][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(40, "_gen6"));
    tree.setList("slist");
    if ( current != null ) {
      if (current.getId() == 12) {
        return tree;
      }
    }
    if (current == null) {
      return tree;
    }
    if (rule == 38) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      subtree = this.parse_step_input();
      tree.add( subtree);
      subtree = this.parse__gen7();
      tree.add( subtree);
      return tree;
    }
    return tree;
  }
  private ParseTree parse_step_input() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[19][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(41, "step_input"));
    tree.setList(null);
    if (current == null) {
      throw new SyntaxError(this.syntaxErrorFormatter.unexpected_eof(
        "step_input",
        Arrays.asList(this.first.get("step_input")),
        this.nonterminal_rules.get("step_input")
      ));
    }
    if (rule == 2) {
      LinkedHashMap<String, Integer> parameters = new LinkedHashMap<String, Integer>();
      parameters.put("parameter", 0);
      parameters.put("value", 2);
      tree.setAstTransformation(new AstTransformNodeCreator("StepInput", parameters));
      next = this.tokens.expect(TerminalId.TERMINAL_IDENTIFIER, "step_input", this.rules.get(2));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_ASSIGN, "step_input", this.rules.get(2));
      tree.add(next);
      subtree = this.parse_variable();
      tree.add( subtree);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("step_input"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "step_input",
      current, 
      Arrays.asList(this.first.get("step_input")),
      this.rules.get(2)
    ));
  }
  private ParseTree parse__gen1() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[20][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(42, "_gen1"));
    tree.setList("nlist");
    if ( current != null ) {
      if (current.getId() == 10) {
        return tree;
      }
    }
    if (current == null) {
      return tree;
    }
    if (rule == 18) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      subtree = this.parse_composite_task_entity();
      tree.add( subtree);
      subtree = this.parse__gen1();
      tree.add( subtree);
      return tree;
    }
    return tree;
  }
  private ParseTree parse__gen0() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[21][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(43, "_gen0"));
    tree.setList("nlist");
    if ( current != null ) {
      if (current.getId() == -1) {
        return tree;
      }
    }
    if (current == null) {
      return tree;
    }
    if (rule == 42) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      subtree = this.parse_wdl_entity();
      tree.add( subtree);
      subtree = this.parse__gen0();
      tree.add( subtree);
      return tree;
    }
    return tree;
  }
  private ParseTree parse_step_output_list() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[22][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(44, "step_output_list"));
    tree.setList(null);
    if (current == null) {
      throw new SyntaxError(this.syntaxErrorFormatter.unexpected_eof(
        "step_output_list",
        Arrays.asList(this.first.get("step_output_list")),
        this.nonterminal_rules.get("step_output_list")
      ));
    }
    if (rule == 15) {
      LinkedHashMap<String, Integer> parameters = new LinkedHashMap<String, Integer>();
      parameters.put("outputs", 2);
      tree.setAstTransformation(new AstTransformNodeCreator("StepOutputList", parameters));
      next = this.tokens.expect(TerminalId.TERMINAL_OUTPUT, "step_output_list", this.rules.get(15));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_COLON, "step_output_list", this.rules.get(15));
      tree.add(next);
      subtree = this.parse__gen8();
      tree.add( subtree);
      next = this.tokens.expect(TerminalId.TERMINAL_SEMI, "step_output_list", this.rules.get(15));
      tree.add(next);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("step_output_list"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "step_output_list",
      current, 
      Arrays.asList(this.first.get("step_output_list")),
      this.rules.get(15)
    ));
  }
  private ParseTree parse_task_attr_value() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[23][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(45, "task_attr_value"));
    tree.setList(null);
    if (current == null) {
      throw new SyntaxError(this.syntaxErrorFormatter.unexpected_eof(
        "task_attr_value",
        Arrays.asList(this.first.get("task_attr_value")),
        this.nonterminal_rules.get("task_attr_value")
      ));
    }
    if (rule == 3) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      next = this.tokens.expect(TerminalId.TERMINAL_STRING, "task_attr_value", this.rules.get(3));
      tree.add(next);
      return tree;
    }
    else if (rule == 17) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      next = this.tokens.expect(TerminalId.TERMINAL_NUMBER, "task_attr_value", this.rules.get(17));
      tree.add(next);
      return tree;
    }
    else if (rule == 31) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      next = this.tokens.expect(TerminalId.TERMINAL_IDENTIFIER, "task_attr_value", this.rules.get(31));
      tree.add(next);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("task_attr_value"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "task_attr_value",
      current, 
      Arrays.asList(this.first.get("task_attr_value")),
      this.rules.get(31)
    ));
  }
  private ParseTree parse_variable() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[24][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(46, "variable"));
    tree.setList(null);
    if (current == null) {
      throw new SyntaxError(this.syntaxErrorFormatter.unexpected_eof(
        "variable",
        Arrays.asList(this.first.get("variable")),
        this.nonterminal_rules.get("variable")
      ));
    }
    if (rule == 20) {
      LinkedHashMap<String, Integer> parameters = new LinkedHashMap<String, Integer>();
      parameters.put("member", 1);
      parameters.put("name", 0);
      tree.setAstTransformation(new AstTransformNodeCreator("Variable", parameters));
      next = this.tokens.expect(TerminalId.TERMINAL_IDENTIFIER, "variable", this.rules.get(20));
      tree.add(next);
      subtree = this.parse__gen10();
      tree.add( subtree);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("variable"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "variable",
      current, 
      Arrays.asList(this.first.get("variable")),
      this.rules.get(20)
    ));
  }
  private ParseTree parse__gen8() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[25][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(47, "_gen8"));
    tree.setList("slist");
    if ( current != null ) {
      if (current.getId() == 12) {
        return tree;
      }
    }
    if (current == null) {
      return tree;
    }
    if (rule == 25) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      subtree = this.parse_step_output();
      tree.add( subtree);
      subtree = this.parse__gen9();
      tree.add( subtree);
      return tree;
    }
    return tree;
  }
  private ParseTree parse_wdl() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[26][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(48, "wdl"));
    tree.setList(null);
    if (current == null) {
      return tree;
    }
    if (rule == 0) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      subtree = this.parse__gen0();
      tree.add( subtree);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("wdl"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "wdl",
      current, 
      Arrays.asList(this.first.get("wdl")),
      this.rules.get(0)
    ));
  }
  private ParseTree parse_composite_task() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[27][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(49, "composite_task"));
    tree.setList(null);
    if (current == null) {
      throw new SyntaxError(this.syntaxErrorFormatter.unexpected_eof(
        "composite_task",
        Arrays.asList(this.first.get("composite_task")),
        this.nonterminal_rules.get("composite_task")
      ));
    }
    if (rule == 28) {
      LinkedHashMap<String, Integer> parameters = new LinkedHashMap<String, Integer>();
      parameters.put("body", 3);
      parameters.put("name", 1);
      tree.setAstTransformation(new AstTransformNodeCreator("CompositeTask", parameters));
      next = this.tokens.expect(TerminalId.TERMINAL_COMPOSITE_TASK, "composite_task", this.rules.get(28));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_IDENTIFIER, "composite_task", this.rules.get(28));
      tree.add(next);
      next = this.tokens.expect(TerminalId.TERMINAL_LBRACE, "composite_task", this.rules.get(28));
      tree.add(next);
      subtree = this.parse__gen1();
      tree.add( subtree);
      next = this.tokens.expect(TerminalId.TERMINAL_RBRACE, "composite_task", this.rules.get(28));
      tree.add(next);
      return tree;
    }
    List<TerminalIdentifier> terminals = Arrays.asList(this.first.get("composite_task"));
    throw new SyntaxError(this.syntaxErrorFormatter.unexpected_symbol(
      "composite_task",
      current, 
      Arrays.asList(this.first.get("composite_task")),
      this.rules.get(28)
    ));
  }
  private ParseTree parse__gen7() throws SyntaxError {
    Terminal current = this.tokens.current();
    Terminal next;
    ParseTree subtree;
    int rule = current != null ? this.table[28][current.getId()] : -1;
    ParseTree tree = new ParseTree( new NonTerminal(50, "_gen7"));
    tree.setList("slist");
    if ( current != null ) {
      if (current.getId() == 12) {
        return tree;
      }
    }
    if (current == null) {
      return tree;
    }
    if (rule == 1) {
      tree.setAstTransformation(new AstTransformSubstitution(0));
      next = this.tokens.expect(TerminalId.TERMINAL_COMMA, "_gen7", this.rules.get(1));
      tree.add(next);
      tree.setListSeparator(next);
      subtree = this.parse_step_input();
      tree.add( subtree);
      subtree = this.parse__gen7();
      tree.add( subtree);
      return tree;
    }
    return tree;
  }
}