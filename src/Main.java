import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {
        Random random = new Random(); //добавляем рандомайзер
        final ArrayList<Integer> a1 = new ArrayList<>();//создаём первый массив
        for (int i = 0; i < 10; i++) {
            a1.add(random.nextInt(20));//заполняем случайными числами в [0,20]
        }
        final ArrayList<Integer> a2 = new ArrayList<>();//создаем второй массив
        for (int i = 0; i < 10; i++) {
            a2.add(random.nextInt(20));//заполняем случайными числами в [0,20]
        }

        //вывод входных данных (массивов)
        System.out.println("Входные данные:");
        System.out.print("a1: ");
        for (Integer i : a1)
            System.out.print(i + " ");
        System.out.println();
        System.out.print("a2: ");
        for (Integer i : a2)
            System.out.print(i + " ");
        System.out.println();


        CompletableFuture<List<Integer>> firstFuture, secondFuture, resultFuture; //создаем 3 объекта класся CompletableFuture

        Iterator<Integer> IteratorA1 = a1.iterator();//добавляем итератор для того, чтобы проходить по массиву и иметь возможноть
        //сразу удалять/добавлять эл-ты
        Iterator<Integer> IteratorA2 = a2.iterator();//добавляем итератор для того, чтобы проходить по массиву и иметь возможноть
        //сразу удалять/добавлять эл-ты

        int max = Collections.max(a1);//ищем максимальное число из первого массива
        System.out.println("\nМаксимальное число из первого массива:" + max);
        double threshold = (max * 80) / 100;//ищем порог удаления эл-ов
        System.out.println("Удаляем эл-нт из первого массива, если он меньше, чем: " + threshold);
        System.out.println("Во втором оставляем кратные трем (3)");



        firstFuture = CompletableFuture.supplyAsync(() -> a1).thenApplyAsync(first -> {//асинхронно делаем вычисления для первого Future

            while (IteratorA1.hasNext()) {//до тех пор, пока в списке есть элементы

                Integer nextInt = IteratorA1.next();//получаем следующий элемент
                if (nextInt < threshold)//если эл-нт меньше порога, удаляем
                    IteratorA1.remove();
            }
            System.out.print("\na1: ");
            Collections.sort(first);
            for (Integer i : first)
                System.out.print(i + " ");
            System.out.println();
            return first;
        });

        secondFuture = CompletableFuture.supplyAsync(() -> a2).thenApplyAsync(second -> {//асинхронно делаем вычисления для второго Future
            while (IteratorA2.hasNext()) {//до тех пор, пока в списке есть элементы

                Integer nextInt = IteratorA2.next();//получаем следующий элемент
                if (!(nextInt % 3 == 0) || nextInt == 0)//если элнт равен 0 или не кратный 3, удаляем
                    IteratorA2.remove();
            }
            Collections.sort(second);
            System.out.print("a2: ");
            for (Integer i : second)
                System.out.print(i + " ");
            System.out.println();
            return second;
        });
        resultFuture = firstFuture.thenCombine(secondFuture, (first, second) -> {//комбинируем наши Future
            List<Integer> a3 = new ArrayList<>();//создаем ещё один динамический массив

            //разность множеств (A1\A2)
            for (Integer i : a1) { //проходим по каждому эл-нту первого массива
                if (!a2.contains(i))//и если такого эл-нта нет во втором массиве
                    a3.add(i);      //добавляем его в 3 массив
            }

            return a3;
        });
        try {
            System.out.println("\nResult(все элементы, которые входят в первый массив и не входят во второй): " + resultFuture.get()); /*blocks until future completes*/
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
