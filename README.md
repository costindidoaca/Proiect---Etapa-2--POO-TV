Proiect-partea2 POO 
Costin Didoaca 

*******************************************************************************
		Descrierea implementarii - Proiect partea 1

	In acest proiect am reusit sa combin design pattern-urile Singleton si
Visitor, folosind Singleton pentru paginile de pe platforma (package
platformpages), baze de date si datele curente (package currentinfo) dar si Visitor
pentru realizarea actiunilor (package visitorpattern), fie ele change page sau
on page. 

	De asemenea am creat clasele de input (package iofiles) cu ajutorul
carora vom prelua datele de intrare in Main si le vom pasa mai departe bazelor 
de date. Apoi vom parsa actiunile si in functie de tipul actiunii cerute vom
folosi unul din visitorii creati care sa realizeze actiunea de pe o pagina
curenta specifica. Dupa parsarea tuturor actiunilor se afiseaza output-ul si se
golesec bazele de date si datele curente pentru a lasa platforma curata (ca la
inceput) pentru urmatorul test.

	ChangingPageVisitor contine metodele doActionFrom*page*(*page type* page,
Action action) care verifica si face, in caz ca e permisa, actiunea de change
page. Toate aceste metode intorc un boolean care ne indica daca actiunea a fost
realizata cu succes sau nu (in functie de asta adaugam un nod specific in output).
Metoda tryToChangePage este universala pentru orice pagina si le include pe toate
cele explicate mai devreme.

	DoingOnPageActionsVisitor contine acelasi set de metode ca la visitorul
pentru actiunile change page, dar de data asta metodele sunt menite sa verifice
daca actiunea de tip on page este permisa si se poate finaliza cu succes, caz in
care metoda intoarce boolean-ul true, iar false in caz contrar. De asemenea, si
aici avem o metoda universala ca sa faciliteze apelarea unica pentru orice pagina
si se numeste tryToDoOnPageAction.

	Interfata visitor contine declararea tuturor metodelor folosite in ambii
visitori. Clasa Constants din package-ul currentinfo e alcatuita din toate
constantele folosite pe parcursul proiectului (ne ajuta sa evitam erori de tip
magic number).

*******************************************************************************
		Descrierea implementarii - Proiect partea 2

	In partea a doua a proiectului am adaugat diferite noi functionalitati
platformei noastre de streaming. Una dintre ele este subscribe adaugata la
actiunile 'on page' exclusiv de pe pagina SeeDetails. Aceasta aboneaza userul
la un anumit gen de film disponibil printre genurile filmului curent. In caz ca
genul dorit nu se afla printre genurile filmului curent sau este deja adaugat
in lista genurilor la care userul este abonat vom afisa eroarea standard.

	Un alt avantaj pe care il aduc schimbarile partii a doua a proiectului
este posibiliatatea de a adauga si de a sterge un film din baza de date a
platformei. Atunci cand se intampla acest lucru sunt actualizate toate listele
userilor, iar cei abonati la un film nou aparut vor fi notificati de aparitia
acestuia.De asemenea, cei ce au cumparat un film ce urmeaza sa fie sters, vor
fi si ei notificati. Notificarea consta in adaugarea in array-ul notifications
al fiecarui user un obiect creat cu ajutorul clasei Notification ce prezinta
titlul filmului adaugat/sters urmat de actiunea corespunzatoare ("ADD"/"DELETE").
De asemnea, aceasa clasa ne este de folos si in momentul cand adaugam o recoman-
dare de film la finalul actiunilor pentru userii premium.

	Nu in ultimul rand, a fost adaugata functionalitatea de 'back' ce ne
permite sa navigam cu o pagina in urma. Acest lucru a fost realizat utilizand
doua stive ce retin paginile si filmele care sunt populate pe masura ce este
finalizata cu succes o actiune de tip 'change page' si se scoate din ele atunci
cand se cere o actiune de tip 'back'. In cazul in care nu este logat niciun user
sau am ajuns la pagina autentificata, daca este ceruta actiunea de tip 'back'
se va afisa o eroare standard. Am implemendat aceasta functionalitate adaugand
un visitor (GoingOnePageBackVisitor) ce este apelat si returneaza in functie
de pagina pe care ne aflam si pagina din urma unde trebuie sa ajungem output-ul
corespunzator.

*******************************************************************************



