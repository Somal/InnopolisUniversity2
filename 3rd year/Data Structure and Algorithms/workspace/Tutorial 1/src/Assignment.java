import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * Maxim Sokolov Bachelor3#5
 * 
 */
public class Assignment {

	/**
	 * This is a class that represents a real 2D point
	 */
	public static class Point2D {
		private double x, y;

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public String toString() {
			return "[" + x + ", " + y + "]";
		}

		public Point2D(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * This is a class that represents rectangle in Cartesian coordinates
	 */
	public static class Rectangle {
		public Rectangle nextRec;
		/** these are corner points of rectangle */
		private Point2D lowerLeft, upperTop;

		private String tag;

		public String getTag() {
			return tag;
		}

		public Rectangle(Point2D lowerLeft, Point2D upperTop, String tag) {
			this.lowerLeft = lowerLeft;
			this.upperTop = upperTop;
			this.tag = tag;
		}

		@Override
		public String toString() {
			return lowerLeft.toString() + "-" + upperTop.toString();
		}

		public Point2D[][] getEdges() {
			Point2D[][] edges = new Point2D[4][2];
			edges[0][0] = lowerLeft;
			edges[0][1] = new Point2D(lowerLeft.getX(), upperTop.getY());
			edges[1][0] = edges[0][1];
			edges[1][1] = upperTop;
			edges[2][0] = upperTop;
			edges[2][1] = new Point2D(upperTop.getX(), lowerLeft.getY());
			edges[3][0] = edges[2][1];
			edges[3][1] = lowerLeft;
			return edges;
		}

		public Rectangle copy() {
			return new Rectangle(lowerLeft, upperTop, tag);
		}
	}

	public static class MyList {
		ListIterator li;
		Rectangle lastRec;
		int size;

		public MyList() {
			size = 0;
			li = new ListIterator();
		}

		public void add(int i, Rectangle rec) {
			Rectangle prevRec = get(i);
			Rectangle nextRec = prevRec.nextRec;
			prevRec.nextRec = rec;
			rec.nextRec = nextRec;
			size++;
		}

		public int size() {
			return size;
		}

		public void addLast(Rectangle rec) {
			if (size == 0) {
				rec.nextRec = rec;
				lastRec = rec;
				size = 1;
				return;
			}
			rec.nextRec = lastRec.nextRec;
			lastRec.nextRec = rec;
			lastRec = rec;
			size++;
		}

		public Rectangle get(int i) {
			Rectangle rec = lastRec;
			for (int j = 0; j < i; j++)
				rec = rec.nextRec;
			return rec.copy();
		}

		public boolean isEmpty() {
			return size == 0;
		}

		public void newIterator() {// creation of Iterator//reset of Iterator
			li = new ListIterator();
		}

		public boolean hasNext() {
			return li.hasNext();
		}

		public Rectangle next() {
			return li.next();
		}

		public void delete(int i) {// delete function i-th point
			Rectangle prevRec = get(i - 1);
			prevRec.nextRec = prevRec.nextRec.nextRec;
			size--;
		}

		public void show() {// printing of list
			System.out.println("------------------");
			/*
			 * Point p = lastPoint.nextPoint; for (int i = 0; i < size; i++) {
			 * System.out.println(p.getX()+" "+p.getY()); p = p.nextPoint; }
			 */
			/*
			 * ListIterator li = new ListIterator(); for (int i = 1; i < size+1;
			 * i++) System.out.println(get(i).getTag());
			 */
			// newIterator();
			for (int i = 1; i <= size; i++) {
				System.out.println(get(i).getTag());
			}
		}

		public class ListIterator implements Iterator<Rectangle> {// inner
																	// Iterator
																	// class
			int index = 1;

			@Override
			public boolean hasNext() {
				return index <= size;
			}

			@Override
			public Rectangle next() {
				index++;
				return get(index - 1);
			}

		}
	}

	public static class MyStack extends MyList {
		public void push(Rectangle rec) {
			addLast(rec);
		}

		public Rectangle pop() {
			if (!isEmpty()) {
				return get(size--);
			} else
				return null;

		}
	}

	public static boolean intersects(Point2D a, Point2D b, Point2D c, Point2D d) {
		double[][] A = new double[2][2];
		A[0][0] = b.getX() - a.getX();
		A[1][0] = b.getY() - a.getY();
		A[0][1] = c.getX() - d.getX();
		A[1][1] = c.getY() - d.getY();
		// calculate determinant
		double det0 = A[0][0] * A[1][1] - A[1][0] * A[0][1];
		// substitute columns and calculate determinants
		double detU = (c.getX() - a.getX()) * A[1][1] - (c.getY() - a.getY()) * A[0][1];
		double detV = A[0][0] * (c.getY() - a.getY()) - A[1][0] * (c.getX() - a.getX());
		// calculate the solution
		// even if det0 == 0 (they are parallel) this will return NaN and
		// comparison will fail -> false
		double u = detU / det0;
		double v = detV / det0;
		return u >= 0 && u <= 1 && v >= 0 && v <= 1;
	}

	public static MyStack getIntersected(MyList rects, Point2D lineStart, Point2D lineFinish) {
		MyStack ms = new MyStack();
		MyList ml = new MyList();
		// boolean p;
		for (int j = 1; j <= rects.size; j++) {
			Rectangle rec = rects.get(j);
			//

			Point2D[][] pp = rec.getEdges();
			// p = false;
			for (int i = 0; i < 4; i++)
				if (intersects(pp[i][0], pp[i][1], lineStart, lineFinish)) {
					// System.out.println(rec.getTag());
					ms.push(rec);
					// ml.addLast(rec);
					break;
				}

			// ml.show();

			/*
			 * System.out.println(); ms.show();
			 */
			// System.out.println(rec.getTag());

		}

		return ms;
	}

	public static MyList getRectangles(String text, Point2D oneLetterSize, Point2D startCorner) {
		MyList list = new MyList();
		String[] words = text.split(" ");
		int position = 0;
		for (String word : words) {
			// System.out.println(word);
			if (word.length() > 0) {
				Point2D ll = new Point2D(startCorner.getX() + position * oneLetterSize.getX(), startCorner.getY());
				Point2D ut = new Point2D(ll.getX() + word.length() * oneLetterSize.getX(),
						startCorner.getY() + oneLetterSize.getY());
				list.addLast(new Rectangle(ll, ut, word));
				// list.show();
			}
			position += word.length() + 1;
		}
		return list;
	}

	public static void main(String[] args) {
		BufferedReader in = null;
		PrintWriter out = null;
		String inputData = "";
		Point2D textCorner, letterSize, lineStart, lineEnd;
		textCorner = letterSize = lineEnd = lineStart = new Point2D(0, 0);

		try {
			in = new BufferedReader(new FileReader("browser.in"));
			out = new PrintWriter(new FileWriter("browser.out"));

			inputData = in.readLine();
			String[] numbers = in.readLine().split(" ");

			textCorner = new Point2D(Double.parseDouble(numbers[0]), Double.parseDouble(numbers[1]));
			letterSize = new Point2D(Double.parseDouble(numbers[2]), Double.parseDouble(numbers[3]));
			lineStart = new Point2D(Double.parseDouble(numbers[4]), Double.parseDouble(numbers[5]));
			lineEnd = new Point2D(Double.parseDouble(numbers[6]), Double.parseDouble(numbers[7]));

		} catch (Exception e) {
			System.out.println("exception");
		}

		/*
		 * MyList list = new MyList(); list.addLast(new Rectangle(new Point2D(0,
		 * 0), new Point2D(0, 0), "first")); list.addLast(new Rectangle(new
		 * Point2D(0, 0), new Point2D(0, 0), "second")); list.show(); MyList
		 * list2=new MyList(); list2.addLast(new Rectangle(new Point2D(0, 0),
		 * new Point2D(0, 0), "3")); list2.addLast(new Rectangle(new Point2D(0,
		 * 0), new Point2D(0, 0), "4")); list2.show();
		 * 
		 * list2.addLast(list.get(2)); //list2.addLast(list.get(2));
		 * list.show(); list2.show();
		 */

		//System.out.println(inputData);
		MyList list = getRectangles(inputData, letterSize, textCorner);
		// list.show();
		MyStack stack = getIntersected(list, lineStart, lineEnd);
		//stack.show();

		while (!stack.isEmpty()) {
			String word = (stack.pop()).getTag();
			System.out.println(word+" ");
			out.write(word+" ");
		}

		try {
			in.close();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
