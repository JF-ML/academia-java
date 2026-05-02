package com.semana2.ejercicio1;

//--- Excepciones ---
class InvalidAmountException extends RuntimeException {
	 // TODO: constructor que reciba mensaje
	 public InvalidAmountException(String message) {
	     super(message);
	 }
}

class InsufficientBalanceException extends Exception {
 private final double deficit;
	
	 // TODO: constructor con mensaje y deficit
	 public InsufficientBalanceException(String message, double deficit) {
	     super(message);
	     this.deficit = deficit;
	 }
	
	 public double getDeficit() { return deficit; }
}

 class AccountLockedException extends Exception {
 // TODO: constructor que reciba mensaje
	public AccountLockedException(String message) {
		super(message);
	}
}

//--- AutoCloseable ---
 class TransactionLog implements AutoCloseable {
	 private boolean open = true;
	
	 public void log(String message) {
	     if (!open) throw new IllegalStateException("Log cerrado");
	     System.out.println("[LOG] " + message);
	 }
	
	 @Override
	 public void close() {
	     // TODO: marcar como cerrado e imprimir mensaje
	     open = false;
	     System.out.println("[LOG] TransactionLog cerrado.");
	 }
}

//--- Cuenta Bancaria ---
public class BankAccount {
 private double balance;
 private boolean locked;

 public BankAccount(double initialBalance) {
	     this.balance = initialBalance;
	     this.locked = false;
	 }
	
	 public void deposit(double amount) {
	     // TODO: si amount <= 0 lanzar InvalidAmountException
	     // TODO: sumar al balance
		 if (amount <= 0) throw new InvalidAmountException("Monto invalido: " + amount);
		 balance+= amount;
	 }
	
	 public void withdraw(double amount) throws InsufficientBalanceException {
	     if (amount <= 0) throw new InvalidAmountException("Monto invalido: " + amount);
	     // TODO: si amount > balance lanzar InsufficientBalanceException con deficit
	     // TODO: restar del balance
	     if (balance < amount) 
	    	 throw new InsufficientBalanceException("Monto invalido: " + amount,balance-amount);
	     balance-=amount;
	 }
	
	 public void transfer(BankAccount target, double amount)
	         throws InsufficientBalanceException {
	     // TODO: usar try-with-resources con TransactionLog
	     //       dentro: withdraw, target.deposit, log ambas operaciones
		 try (TransactionLog log = new TransactionLog()) {
			 this.withdraw(amount);
			 log.log("Retiro de $"+amount+" de cuenta origen. Saldo:$"+this.getBalance());
			 target.deposit(amount);
			 log.log("Deposito de $"+amount+" de cuenta destino. Saldo:$"+target.getBalance());
		 }
	 }
	
	 public void lock() { this.locked = true; }
	 public double getBalance() { return balance; }
	
	 public static void main(String[] args) {
	     BankAccount cuenta1 = new BankAccount(1000.00);
	     BankAccount cuenta2 = new BankAccount(500.00);
	
	     // Operaciones validas
	     try {
	         cuenta1.deposit(500);
	         System.out.printf("Deposito exitoso. Saldo: $%.2f%n", cuenta1.getBalance());
	
	         cuenta1.withdraw(200);
	         System.out.printf("Retiro exitoso. Saldo: $%.2f%n", cuenta1.getBalance());
	
	         cuenta1.transfer(cuenta2, 300);
	         System.out.printf("Transferencia exitosa. Saldo cuenta1: $%.2f, cuenta2: $%.2f%n",
	             cuenta1.getBalance(), cuenta2.getBalance());
	     } catch (InsufficientBalanceException e) {
	         System.out.println("Error: " + e.getMessage());
	     }
	
	     System.out.println("\n=== Manejo de Errores ===");
	
	     // TODO: multi-catch para monto invalido
	     try {
	         cuenta1.deposit(-100);
	     } catch (InvalidAmountException e) {
	         System.out.println("Error: " + e.getMessage());
	     }
	
	     // TODO: fondos insuficientes con deficit
	     try {
	         cuenta1.withdraw(999999);
	     } catch (InsufficientBalanceException e) {
	         System.out.printf("Error: %s (deficit: $%.2f)%n",
	             e.getMessage(), e.getDeficit());
	     }
	 }
}