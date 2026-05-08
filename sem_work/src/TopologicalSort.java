import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class TopologicalSort{
    // Простой секундомер на System.nanoTime
    static class Stopwatch {
        private long startNs;
        void start() {
            startNs = System.nanoTime();
        }
        double stopMillis() {
            return (System.nanoTime() - startNs) / 1_000_000.0;
        }
    }
    static void generateData(String dir, int count, int nMin, int nMax) throws IOException {
        File d = new File(dir);
        if (!d.exists() && !d.mkdirs()) {
            throw new IOException("Не удалось создать каталог: " + dir);
        }
        Random rand = new Random(42);
        for (int i = 0; i < count; i++) {
            int n = nMin + (int) ((long) (nMax - nMin) * i / Math.max(1, count - 1));
            int targetEdges = Math.min((long) n * 4, (long) n * (n - 1) / 2) > Integer.MAX_VALUE
                    ? Integer.MAX_VALUE
                    : (int) Math.min((long) n * 4, (long) n * (n - 1) / 2);
            Set<Long> seen = new HashSet<>();
            List<int[]> edges = new ArrayList<>(targetEdges);
            int attempts = 0, maxAttempts = targetEdges * 10;
            while (edges.size() < targetEdges && attempts++ < maxAttempts) {
                int u = rand.nextInt(n);
                int v = rand.nextInt(n);
                if (u == v) continue;
                if (u > v) {
                    int t = u;
                    u = v;
                    v = t;
                }
                long key = (long) u * n + v;
                if (seen.add(key)) edges.add(new int[]{u, v});
            }
            Integer[] permBoxed = IntStream.range(0, n).boxed().toArray(Integer[]::new);
            List<Integer> permList = Arrays.asList(permBoxed);
            Collections.shuffle(permList, rand);
            int[] perm = new int[n];
            for (int k = 0; k < n; k++) perm[k] = permBoxed[k];

            String filePath = dir + "/graph_" + String.format("%03d", i) + ".txt";
            try (PrintWriter pw = new PrintWriter(filePath)) {
                pw.println(n);
                pw.println(edges.size());
                for (int[] e : edges) {
                    pw.println(perm[e[0]] + " " + perm[e[1]]);
                }
            }
        }
        System.out.println("Генерация завершена.");
    }

    // Топосортировка на forward-star (массивы head/next/to). Возвращает число итераций.

    static long topoSortArray(int n, int[] head, int[] next, int[] to) {
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) {
            for (int e = head[u]; e != -1; e = next[e]) {
                indeg[to[e]]++;
            }
        }
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int v = 0; v < n; v++) if (indeg[v] == 0) queue.add(v);

        long iter = 0;
        int processed = 0;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            processed++;
            for (int e = head[u]; e != -1; e = next[e]) {
                iter++;
                if (--indeg[to[e]] == 0) queue.add(to[e]);
            }
        }
        if (processed != n) throw new IllegalStateException("Граф содержит цикл");
        return iter;
    }

    // Топосортировка на List<List<Integer>>. Возвращает число итераций.
    static long topoSortList(int n, List<List<Integer>> adj) {
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) {
            for (int v : adj.get(u)) indeg[v]++;
        }
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int v = 0; v < n; v++) if (indeg[v] == 0) queue.add(v);

        long iter = 0;
        int processed = 0;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            processed++;
            for (int v : adj.get(u)) {
                iter++;
                if (--indeg[v] == 0) queue.add(v);
            }
        }
        if (processed != n) throw new IllegalStateException("Граф содержит цикл");
        return iter;
    }

    static void runTests() {
        int curn = 0;
        System.out.println("Тесты:");
        // 1. Пустой граф
        if (run("Пустой", () -> {
            assert topoSortArray(0, new int[0], new int[0], new int[0]) == 0;
            return true;
        })) curn++;

        // 2. Одна вершина
        if (run("1 вершина", () -> {
            assert topoSortArray(1, new int[]{-1}, new int[0], new int[0]) == 0;
            return true;
        })) curn++;

        // 3. Цепочка 0→1→2 (3 итерации)
        if (run("Цепочка", () -> {
            int n=3, m=2;
            int[] h=new int[n], t=new int[m], nx=new int[m]; Arrays.fill(h,-1);
            t[0]=1; nx[0]=h[0]; h[0]=0;  // 0→1
            t[1]=2; nx[1]=h[1]; h[1]=1;  // 1→2
            return topoSortArray(n,h,nx,t) == 2;
        })) curn++;

        // 4. Разветвление: 0→1, 0→2 (2 итерации)
        if (run("Разветвление", () -> {
            int n=3, m=2;
            int[] h=new int[n], t=new int[m], nx=new int[m]; Arrays.fill(h,-1);
            t[0]=1; nx[0]=h[0]; h[0]=0;
            t[1]=2; nx[1]=h[0]; h[0]=1;
            return topoSortArray(n,h,nx,t) == 2;
        })) curn++;

        // 5. Цикл должен выбросить исключение
        if (run("Цикл", () -> {
            int n=3, m=3;
            int[] h=new int[n], t=new int[m], nx=new int[m]; Arrays.fill(h,-1);
            t[0]=1; nx[0]=h[0]; h[0]=0;
            t[1]=2; nx[1]=h[1]; h[1]=1;
            t[2]=0; nx[2]=h[2]; h[2]=2;
            try { topoSortArray(n,h,nx,t); return false; }
            catch (IllegalStateException e) { return true; }
        })) curn++;

        // 6. Array и List дают одинаковые итерации
        if (run("Array и List дают одинаковые итерации", () -> {
            int n=5, m=4;
            int[] h=new int[n], ta=new int[m], nx=new int[m]; Arrays.fill(h,-1);
            List<List<Integer>> adj = new ArrayList<>();
            for(int i=0;i<n;i++) adj.add(new ArrayList<>());
            int[][] edges = {{0,1},{0,2},{1,3},{2,4}};
            for(int i=0;i<m;i++){
                int u=edges[i][0], v=edges[i][1];
                ta[i]=v; nx[i]=h[u]; h[u]=i;
                adj.get(u).add(v);
            }
            return topoSortArray(n,h,nx,ta) == topoSortList(n,adj);
        })) curn++;
        System.out.printf("Результат: %d/6%n", curn);
        System.out.println("В тесте участвовало 6 разных графов.");
        if (curn < 6) System.exit(1);
    }
    static boolean run(String name, java.util.function.Supplier<Boolean> test) {
        try {
            if (test.get()) { System.out.println("  ✓ " + name); return true; }
        } catch (Exception e) {}
        System.out.println("  ✗ " + name);
        return false;
    }

    public static void main(String[] args) throws Exception {
        runTests();

        String dataDir = "topo_data";
        String resultFile = "results.csv";

        // 1. Подготовка данных
        generateData(dataDir, 70, 100, 10000);

        File dir = new File(dataDir);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null) return;
        Arrays.sort(files);

        Stopwatch sw = new Stopwatch();

        try (PrintWriter out = new PrintWriter(resultFile)) {
            out.println("Size,Time_Array_ms,Iter_Array,Time_List_ms,Iter_List");
            System.out.println("Запуск...");

            int warmupCount = Math.min(5, files.length);
            for (int w = 0; w < warmupCount; w++) {
                Object[] parsed = readGraph(files[w]);
                int n = (int) parsed[0];
                int[] head = (int[]) parsed[1];
                int[] next = (int[]) parsed[2];
                int[] to = (int[]) parsed[3];
                @SuppressWarnings("unchecked")
                List<List<Integer>> adjList = (List<List<Integer>>) parsed[4];
                topoSortArray(n, head, next, to);
                topoSortList(n, adjList);
            }

            for (File f : files) {
                Object[] parsed = readGraph(f);
                int n = (int) parsed[0];
                int[] head = (int[]) parsed[1];
                int[] next = (int[]) parsed[2];
                int[] to = (int[]) parsed[3];
                @SuppressWarnings("unchecked")
                List<List<Integer>> adjList = (List<List<Integer>>) parsed[4];

                // Остановка массивов
                sw.start();
                long iterArray = topoSortArray(n, head, next, to);
                double timeArray = sw.stopMillis();

                // Остановка List<T>
                sw.start();
                long iterList = topoSortList(n, adjList);
                double timeList = sw.stopMillis();

                out.printf(Locale.US, "%d,%.4f,%d,%.4f,%d%n",
                        n, timeArray, iterArray, timeList, iterList);
                System.out.printf(Locale.US,
                        "[%-15s] N=%-5d | Array: %7.4fms (%-7d it) | List: %7.4fms (%-7d it)%n",
                        f.getName(), n, timeArray, iterArray, timeList, iterList);
            }
            System.out.println();
            System.out.println("Результаты сохранены в: " + resultFile);
            System.out.println("Файл готов для импорта в MS Excel.");
        }
    }

    // Чтение графа в обе структуры одновременно.
    private static Object[] readGraph(File f) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            int n = Integer.parseInt(br.readLine().trim());
            int m = Integer.parseInt(br.readLine().trim());

            int[] head = new int[n];
            Arrays.fill(head, -1);
            int[] next = new int[m];
            int[] to = new int[m];

            List<List<Integer>> adjList = new ArrayList<>(n);
            for (int i = 0; i < n; i++) adjList.add(new ArrayList<>());

            for (int i = 0; i < m; i++) {
                StringTokenizer st = new StringTokenizer(br.readLine());
                int u = Integer.parseInt(st.nextToken());
                int v = Integer.parseInt(st.nextToken());
                to[i] = v;
                next[i] = head[u];
                head[u] = i;
                adjList.get(u).add(v);
            }
            return new Object[]{n, head, next, to, adjList};
        }
    }
}