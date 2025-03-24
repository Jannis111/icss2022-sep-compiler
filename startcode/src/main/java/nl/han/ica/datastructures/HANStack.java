package nl.han.ica.datastructures;

import java.util.ArrayList;
import java.util.LinkedList;

public class HANStack<T> implements IHANStack<T>
{
    private LinkedList<T> list;

    public HANStack() {
        this.list = new LinkedList<>();
    }

    // Push: Add to the front of the list (LIFO)
    @Override
    public void push(T value)
    {
        list.add(value);
    }

    // Pop: Remove and return the first element (top of stack)
    @Override
    public T pop()
    {
        if (list.isEmpty())
        {
            throw new IllegalStateException("Stack is empty");
        }

        return list.remove(list.size() - 1);
    }

    // Peek: Return the top element without removing it
    @Override
    public T peek()
    {
        if (list.isEmpty())
        {
            throw new IllegalStateException("Stack is empty");
        }

        return list.get(list.size() - 1);
    }

}
