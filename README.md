
# Wallet-Service  
  
*Wallet-Service* - это консольное приложение для управления кредитными и дебетовыми транзакциями от имени игроков.  
  
## Инструкции по использованию  
  
Для запуска приложения выполните следующие шаги:  
  
1.  Скачайте JAR-файл приложения с репозитория (или скомпилируйте его самостоятельно).
    
3.  Откройте командную строку (терминал) и перейдите в каталог, где находится JAR-файл.
    
4.  Запустите приложение, выполнив следующую команду:
    
    `(ваш путь к java)\bin\java -jar yLabProject.jar` 
    
    Приложение будет запущено в консоли.
    

## Основные функции

Приложение предоставляет следующие основные функции:

-   **Регистрация игрока**: Вы можете зарегистрировать нового игрока, предоставив его логин и пароль.
    
-   **Авторизация игрока**: После регистрации вы можете авторизоваться с помощью вашего логина и пароля.
    
-   **Просмотр текущего баланса игрока**: После авторизации вы сможете узнать текущий баланс своего аккаунта.
    
-   **Дебет/Снятие средств**: Вы можете совершать дебетовые транзакции для снятия средств со счета игрока. Дебетовая транзакция будет успешной только в том случае, если на счету достаточно средств. Также, каждая транзакция должна иметь уникальный идентификатор.
    
-   **Кредит**: Вы можете совершать кредитные транзакции для пополнения счета игрока. Как и в случае с дебетовыми транзакциями, каждая кредитная транзакция должна иметь уникальный идентификатор.
    
-   **Просмотр истории транзакций**: После авторизации вы можете просматривать историю пополнения и снятия средств со счета игрока.
    
-   **Аудит действий**: Приложение ведет аудит действий игроков, включая авторизацию, завершение работы, пополнения и снятия средств, и многое другое.
    

## Логирование

Логи приложения сохраняются в папке, которая создается в том же месте, откуда был запущен JAR-файл. 