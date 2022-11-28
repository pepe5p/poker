Wybrałem wariant pokera: `No Limit Poker Texas Hold'em`.
Serwer obsługuje wiele gier na raz. Gry muszą mieć od 2 do 9 graczy.

Zasady:
 https://pl.wikipedia.org/wiki/Texas_Hold%E2%80%99em
 https://www.pokerzasady.pl/

Uruchamianie:
 - włączenie serwera:
  $ java -jar .\server\server-1.0-SNAPSHOT-jar-with-dependencies.jar
 - włączenie dowolnej ilości klientów:
  $ java -jar .\client\client-1.0-SNAPSHOT-jar-with-dependencies.jar

 Następnie, żeby zagrać należy stworzyć grę w jednym kliencie i dołaczyć do niej w drugim:
  client1 terminal: create 25 100 10000
  client2 terminal: join 1 10000
  client1 terminal: start

Protokół Komunikacji:
 Komendy dzielą się na te, które można użyć podczas gry oraz poza nią.
 Jeśli użyje się komendy w nieodpowiednim miejscu serwer wyśle wiadomość:
  'you are not in game' albo 'you are currently in game'.
 Wszystkie argumenty komend to liczby, stąd gdy wyślemy argument inny niż Integer otrzymamy:
  '<nazwa akcji> arguments should be a number, but <niepoprawny argument> was provided'.

 Komendy, których może używać klient poza grą to:
  * close-server <close phrase> - wyłącza serwer <close phrase> to '2137'.
  * create <ante> <small blind> <ilość żetonów> - tworzy grę, oznajmia o tym wszystkim aktualnie
   podłączonym klientom. Automatycznie dodaje gracza, który stworzył grę (nie trzeba używać join),
   np. 'create 25 100 10000'.
  * join <id gry> <ilość żetonów> - umożliwia dołączenie do wybranej gry. Gracze w wybranej grze
   otrzymują wiadomość: '<nazwa gracza> joined', np. 'join 1 10000'.

 Komendy, których może używać klient w grze to:
  * start - rozpoczyna grę, jeżeli liczba graczy jest w przedziale <2, 9>.
  * quit - usuwa gracza z gry. Teraz może używać komend do użycia poza grą.
  * call - zwykły pokerowy call (uznaje 'check' za 'call 0'). Jeśli nie jest aktualnie tura gracza
   to call zostanie wykonany w momencie, gdy będzie tura gracza. Call większy niż ilość żetonów gracza
   traktowany jest jako all-in.
  * raise <ilość żetonów> - zwykły pokerowy raise. Jeśli nie jest aktualnie tura gracza to raise
   zostanie wykonany w momencie, gdy będzie tura gracza. Raise wiekszy niż ilość żetonów gracz
   traktowany jest jako all-in.
  * fold - zwykły pokerowy fold. Jeśli nie jest aktualnie tura gracza to fold
   zostanie wykonany w momencie, gdy będzie tura gracza.
  * all-in - zwykły pokerowy all-in. Jeśli nie jest aktualnie tura gracza to all-in
   zostanie wykonany w momencie, gdy będzie tura gracza.
  * clear - usuwa zakolejkowany ruch (call, raise, fold, all-in).
  * status - pokazuje aktualny status gry. Zostaje on też pokazany w momencie kiedy gracz na turze wykonuje ruch.
   Odpowiedź jest postaci np.
        > (only you) not your turn
        [A♥][K♣][A♦] on table
        DE A [user1] 0 in stack / 10000 in pot        # DE oznacza dealera, A oznacza all-in
        SB * [user3] 9875 in stack / 125 in pot       # SB oznacza small blind, * oznacza aktualną turę
        BB   [user4] 9775 in stack / 225 in pot       # BB oznacza big blind
           F [user2] 10000 in stack / 0 in pot        # F oznacza że gracz zfoldował
        10350 in pot / 0 to call
        [K♥][A♣] Full House on A, K

  Każda inna wiadomość wysłana do serwera spotka się z odpowiedzią 'action not recognized'.

  Sa 3 rodzaje odpowiedzi serwera:
  > (only you) - wysyłane tylko do jednego klienta np. po wykonaniu komendy status
  > (game) - wysyłane do wszystkich klientów w grze np. po wykonaniu komendy join
  > (everyone) - wysyłane do wszystkich klientów np. po wykonaniu komendy create
