import java.util.*;
import java.io.*;
import com.murcia.utils.*; 

public class Main {
    public static void main(String[] args) throws Exception {
        File file = new File("randomtext.txt");
        Scanner sc = new Scanner(file);

        ColaEnlazada<StringBuilder> l = new ColaEnlazada<>();

        while(sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] words = line.split("\\s");
            for(String i : words) {
                l.encolar(new StringBuilder(i));
            }
            l.encolar(new StringBuilder("\n"));
        }

        Editor edit = new Editor(l);
        sc = new Scanner(System.in);

        String[] opciones ={
            "S - Save and Exit",
            "F - Move Forward",
            "B - Move Backward",
            "b - Move to Beginning of line",
            "e - Move to End of Line",
            "i - Insert",
            "d - DElete"
        };
        Menu menu = new Menu(opciones, 'V', "\n", "Editor de texto");
       
        while(true) {
            Consola.clrscr();
            System.out.println(menu.toString());

            edit.print();
            edit.printCursor();

            String input = sc.nextLine();
            if(input.equals("S")) break;
            else if(input.equals("F")) edit.forward();
            else if(input.equals("B")) edit.backward();
            else if(input.equals("b")) edit.beginning();
            else if(input.equals("e")) edit.ending();
            else if(input.equals("i")) edit.insert(sc.nextLine());
            else if(input.equals("d")) edit.delete();

            file.delete();
            file.createNewFile();
        }
        edit.writer.close();
    }
}

class Editor {
    ColaEnlazada<StringBuilder> l;
    int node, position;
    FileWriter writer;

    public Editor(ColaEnlazada<StringBuilder> l) {
        this.l = l;
        node = position = 0;
    }

    public void forward() {
        if(l.size() == 0) return;
        if(node >= l.size()) node = l.size() - 1;

        StringBuilder s = l.get(node);
        if(position >= s.length()) {
            if(node == l.size() - 1) node = 0;
            else node++;
            position = 0;
        } else position++;
        if(node < l.size()) {
            s = l.get(node);
            if(s.toString().equals("\n")) forward();
        }
    }

    public void backward() {
        if(l.size() == 0) return;
        if(node >= l.size()) node = l.size() - 1;

        StringBuilder s = l.get(node);
        if(position == 0) {
            if(node == 0) node = l.size() - 1;
            else node--;
            s = l.get(node);
            position = s.length();
        } else position--;
        if(node < l.size()) {
            s = l.get(node);
            if(s.toString().equals("\n")) backward();
        }
    }

    public void insert(String input) {
        if(l.size() == 0) return;
        if(node >= l.size()) node = l.size() - 1;

        StringBuilder s = l.get(node);
        if(s.length() == 0) s.append(input);
        else s.insert(Math.min(position, s.length()), input);
        position = Math.min(position + input.length(), s.length());
    }

    public void delete() {
        if(l.size() == 0) return;
        if(node >= l.size()) node = l.size() - 1;

        StringBuilder s = l.get(node);
        if(position == 0 && node > 0) {
            StringBuilder prev = l.get(node - 1);
            position = prev.length();
            prev.append(s);
            l.remove(node);
            if(node >= l.size()) node = l.size() - 1;
        } else if(position > 0 && position <= s.length()) {
            s.delete(position - 1, position);
            position--;
        }
    }

    public void beginning() {
        if(l.size() == 0) return;
        while(node > 0 && !l.get(node).toString().equals("\n")) node--;
        if(node != 0) node++;
        position = 0;
    }

    public void ending() {
        if(l.size() == 0) return;
        while(node < l.size() && !l.get(node).toString().equals("\n")) node++;
        if(node > 0) node--;
        if(node < l.size()) position = l.get(node).length();
    }

    public void print() throws Exception {
        writer = new FileWriter("randomtext.txt");
        for(int i = 0; i < l.size(); i++) {
            StringBuilder sb = l.get(i);
            writer.write(sb.toString());
            System.out.print(sb);
            if(!sb.toString().equals("\n")) {
                writer.write(" ");
                System.out.print(" ");
            }
        }
    }

    public void printCursor() {
        if(l.size() == 0) {
            System.out.println("Cursor At : |");
            return;
        }
        if(node < 0) node = 0;
        if(node >= l.size()) node = l.size() - 1;

        System.out.print("Cursor At : ");
        StringBuilder s = l.get(node);
        for(int i = 0; i < s.length(); i++) {
            if(i == position) System.out.print("|");
            System.out.print(s.charAt(i));
        }
        if((position == 0 && s.length() == 0) || (position == s.length())) System.out.print("|");
        System.out.println();
    }
}

