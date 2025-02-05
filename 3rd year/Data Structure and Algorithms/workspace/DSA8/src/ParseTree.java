import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseTree extends MyTree<Object> {// k-ary tree
	HashMap<Character, Integer> list = new HashMap<Character, Integer>();

	public ParseTree(String inputString) {
		super(2);
		list.put('-', 1);
		list.put('+', 1);
		list.put('*', 2);
		list.put('/', 2);

		parseString(inputString, 0);
		//this.postorderTraversal(0);
		//System.out.println();
		calculating(0);
	}

	public double result() {
		return (Double) get(0);
	}

	public int characterAnalyser(char c) {
		int ans = 0;
		if (c == ')')
			ans = -1;
		if (c == '(')
			ans = 1;
		return ans;
	}

	public void parseString(String input, int index) {
		//System.out.println("input " + input);
		int start = 0;
		int end = 0;
		int i = 0;
		int b = 0;

		while (i < input.length() & b == 0) {
			b += characterAnalyser(input.charAt(i));
			i++;
		}
		start = i - 1;
		while (i < input.length() & b != 0) {
			b += characterAnalyser(input.charAt(i));
			i++;
		}
		end = i - 1;
		// System.out.println(input + " " + start + " " + end);

		if (end != 0 & start < end) {
			String substring = input.substring(start + 1, end);
			//System.out.println(input + " " + start + " " + end);
			// System.out.println(substring);
			ParseTree tree = new ParseTree(substring);

			String result = Double.toString(tree.result());
			this.parseString(input.substring(0, start) + result + input.substring(end + 1, input.length()), index);
		} else {
			// recursive traversal
			for (int k = 2; k > 0; k--)
				for (int j = input.length() - 1; j >= 0; j--) {
					char del = input.charAt(j);
					Integer res = this.list.get(del);
					if (res == null)
						continue;
					if (res == k) {
						// System.out.println(j);
						this.set(index, del);
						// this.postorderTraversal(0);
						// System.out.println();
						parseString(input.substring(0, j), childIndex(index, 0));
						parseString(input.substring(j + 1, input.length()), childIndex(index, 1));
						return;
					}
				}

			if (input.length() == 0 || input.split(" ").length==0) {
				//System.out.println("input empty !" + input + "!");
				this.set(index, 0);
			} else {
				// System.out.println(input);
				//System.out.println("input not empty !" + input + "!");
				this.set(index, Double.parseDouble(input));

			}

		}
	}



	String change(int l, int r, String oldString, String newString) {
		return oldString.substring(0, l) + newString + oldString.substring(r, oldString.length());
	}

	public void deleteSubtreeFrom(int index) {
		for (int j = 0; j < this.k; j++) {
			int childIndex = childIndex(index, j);
			if (get(childIndex) != null)
				deleteSubtreeFrom(childIndex);
		}
		set(index, null);
	}

	<T> double commonConverter(T data) {
		double output = 0;
		if (Integer.class.isInstance(data))
			output = new Integer((int) data);
		if (Double.class.isInstance(data))
			output = new Double((double) data);
		return output;
	}

	public void calculating(int i) {
		for (int j = 0; j < 2; j++) {
			int child = childIndex(i, j);
			if (get(child) != null)
				calculating(child);
		}

		// System.out.println("index " + i + " get(i)" + get(i).toString()+"!");

		Object result = null;
		if (Character.class.isInstance(get(i))) {
			switch ((Character) get(i)) {
			case '+':
				result = commonConverter(get(childIndex(i, 0))) + commonConverter(get(childIndex(i, 1)));
				break;
			case '-':
				result = commonConverter(get(childIndex(i, 0))) - commonConverter(get(childIndex(i, 1)));
				break;
			case '*':
				result = commonConverter(get(childIndex(i, 0))) * commonConverter(get(childIndex(i, 1)));
				break;
			case '/':
				result = commonConverter(get(childIndex(i, 0))) / commonConverter(get(childIndex(i, 1)));
				break;
			}
			// System.out.println("it is string");

		} else
			result = commonConverter(get(i));

		// System.out.println("result " + (double) result);
		deleteSubtreeFrom(i);
		set(i, result);

		// System.out.println(get(i).toString());
		// this.postorderTraversal(0);
		// System.out.println();
	}

}
