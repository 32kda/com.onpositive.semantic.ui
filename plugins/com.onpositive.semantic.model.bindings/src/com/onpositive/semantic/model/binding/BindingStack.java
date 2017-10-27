package com.onpositive.semantic.model.binding;

import java.util.Stack;

public class BindingStack {

	private static ThreadLocal<Stack<IBinding>> bindingStack = new ThreadLocal<Stack<IBinding>>();

	public static IBinding getCaller() {
		final Stack<IBinding> stack = bindingStack.get();
		if (stack == null) {
			return null;
		}
		if (stack.isEmpty()) {
			return null;
		}
		return stack.peek();
	}
	public static IBinding getCaller(int ac) {
		final Stack<IBinding> stack = bindingStack.get();
		if (stack == null) {
			return null;
		}
		if (stack.isEmpty()) {
			return null;
		}
		return stack.get(stack.size()-ac);
	}

	protected static void callStarted(IBinding bnd) {
		Stack<IBinding> stack = bindingStack.get();
		if (stack == null) {
			stack = new Stack<IBinding>();
			bindingStack.set(stack);
		}
		stack.push(bnd);
	}

	protected static void callEnded(IBinding bnd) {
		Stack<IBinding> stack = bindingStack.get();
		if (stack == null) {
			stack = new Stack<IBinding>();
			bindingStack.set(stack);
		}
		stack.pop();
	}

}
