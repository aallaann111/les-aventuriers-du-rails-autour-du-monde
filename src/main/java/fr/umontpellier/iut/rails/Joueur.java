package fr.umontpellier.iut.rails;

import com.sun.tools.javac.Main;
import fr.umontpellier.iut.rails.data.*;

import java.util.*;
import java.util.stream.Collectors;

public class Joueur {
    public enum CouleurJouer {
        JAUNE, ROUGE, BLEU, VERT, ROSE;
    }

    /**
     * Jeu auquel le joueur est rattaché
     */
    private final Jeu jeu;
    /**
     * Nom du joueur
     */
    private final String nom;
    /**
     * CouleurJouer du joueur (pour représentation sur le plateau)
     */
    private final CouleurJouer couleur;
    /**
     * Liste des villes sur lesquelles le joueur a construit un port
     */
    private final List<Ville> ports;
    /**
     * Liste des routes capturées par le joueur
     */
    private final List<Route> routes;
    /**
     * Nombre de pions wagons que le joueur peut encore poser sur le plateau
     */
    private int nbPionsWagon;
    /**
     * Nombre de pions wagons que le joueur a dans sa réserve (dans la boîte)
     */
    private int nbPionsWagonEnReserve;
    /**
     * Nombre de pions bateaux que le joueur peut encore poser sur le plateau
     */
    private int nbPionsBateau;
    /**
     * Nombre de pions bateaux que le joueur a dans sa réserve (dans la boîte)
     */
    private int nbPionsBateauEnReserve;
    /**
     * Liste des destinations à réaliser pendant la partie
     */
    private final List<Destination> destinations;
    /**
     * Liste des cartes que le joueur a en main
     */
    private final List<CarteTransport> cartesTransport;
    /**
     * Liste temporaire de cartes transport que le joueur est en train de jouer pour
     * payer la capture d'une route ou la construction d'un port
     */
    private final List<CarteTransport> cartesTransportPosees;
    /**
     * Score courant du joueur (somme des valeurs des routes capturées,et points
     * perdus lors des échanges de pions)
     */
    private String MainTour;
    private int score;

    private Collection<Bouton> boutons;
    private Collection<String> unChoix;

    public Joueur(String nom, Jeu jeu, CouleurJouer couleur) {
        this.nom = nom;
        this.jeu = jeu;
        this.couleur = couleur;
        this.ports = new ArrayList<>();
        this.routes = new ArrayList<>();
        this.nbPionsWagon = 0;
        this.nbPionsWagonEnReserve = 25;
        this.nbPionsBateau = 0;
        this.nbPionsBateauEnReserve = 50;
        this.cartesTransport = new ArrayList<>();
        this.cartesTransportPosees = new ArrayList<>();
        this.destinations = new ArrayList<>();
        this.score = 0;
    }

    public String getNom() {
        return nom;
    }

    public int getNbPionsWagon() {
        return nbPionsWagon;
    }

    public int getNbPionsWagonEnReserve() {
        return nbPionsWagonEnReserve;
    }

    public int getNbPionsBateau() {
        return nbPionsBateau;
    }

    public int getNbPionsBateauEnReserve() {
        return nbPionsBateauEnReserve;
    }

    public void setNbPionsWagon(int nbPionsWagon) {
        this.nbPionsWagon = nbPionsWagon;
    }

    public void setNbPionsBateau(int nbPionsBateau) {
        this.nbPionsBateau = nbPionsBateau;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }


    //choisir le nombre de pions wagon avec la fonction choisir()
    //il faut choisir plus de 9 pions wagon et moins de 26 pions wagon
    public void choisirNbPionsWagon() {
        List<String> options = new ArrayList<>();
        for (int i = 10; i < 26; i++) {
            options.add(String.valueOf(i));
        }
        String choix = choisir(
                "Choisissez le nombre de pions wagon que vous voulez",
                options,
                null,
                false);
        nbPionsWagon = Integer.parseInt(choix);
        nbPionsWagonEnReserve -= nbPionsWagon;

        nbPionsBateau = 60 - nbPionsWagon;
        nbPionsBateauEnReserve -= nbPionsBateau;
        log(String.format("%s a choisis " + nbPionsBateau + " pions bateau et " + nbPionsWagon + " pions wagon", toLog()));

    }


    /**
     * Cette méthode est appelée à tour de rôle pour chacun des joueurs de la partie.
     * Elle doit réaliser un tour de jeu, pendant lequel le joueur a le choix entre 5 actions possibles :
     * - piocher des cartes transport (visibles ou dans la pioche)
     * - échanger des pions wagons ou bateau
     * - prendre de nouvelles destinations
     * - capturer une route
     * - construire un port
     */
    //le joueur peut choisir entre 5 actions possibles
    //il faut choisir une action valide
    //- piocher des cartes transport (visibles ou dans la pioche)
    private boolean tours = true;
    void jouerTour() {
    do {
        List<String> options = new ArrayList<>(Arrays.asList("WAGON", "BATEAU", "DESTINATION"));
        List<Bouton> boutons = Arrays.asList(
                new Bouton("Echanger des pions  bateau", "PIONS BATEAU"),
                new Bouton("Echanger des pions wagon", "PIONS WAGON")

        );

        List<String> routesLibres = jeu.getRoutesLibres().stream().map(Route::getNom).toList();
        List<String> nomPort = jeu.getPortsLibres().stream().map(Ville::getNom).toList();
        List<String> nomsCartesTransportVisibles = jeu.getCartesTransportVisibles().stream().map(CarteTransport::getNom).toList();
        options.addAll(routesLibres);
        options.addAll(nomPort);
        options.addAll(nomsCartesTransportVisibles);


        MainTour = choisir(
                "Que voulez-vous faire ?",
                options,
                boutons,
                true);

        if (MainTour.equals("WAGON")) {
            log(String.format("%s a choisi choisi de piocher une carte wagon", toLog()));
            prendreCarteWagon();
        } else if (MainTour.equals("BATEAU")) {
            log(String.format("%s a choisi choisi de piocher une carte bateau", toLog()));
            prendreCarteBateau();
        } else if (nomsCartesTransportVisibles.contains(MainTour)) {
            CarteTransport carteChoisie = null;
            for (CarteTransport c : jeu.getCartesTransportVisibles()) {
                if (c.getNom().equals(MainTour)) {
                    carteChoisie = c;
                    cartesTransport.add(carteChoisie);
                    jeu.getCartesTransportVisibles().remove(carteChoisie);
                    break;
                }
                choixCarte();
                piocher2emeCarte();
            }
        } else if (MainTour.equals("PIONS WAGON")) {
            log(String.format("%s a choisi de changer ses pions wagon", toLog()));
            echangerWagon();
        } else if (MainTour.equals("PIONS BATEAU")) {
            log(String.format("%s a choisi de changer ses pions bateau", toLog()));
            echangerBateau();
        } else if (MainTour.equals("DESTINATION")) {
            log(String.format("%s a choisi choisi de prendre de nouvelles destinations", toLog()));
            prendreDestinations();
        }
    } while (tours);

    }

    public void piocher2emeCarte() {

        List<String> nomsCartesTransportVisibles = new ArrayList<>();

        List<String> options = new ArrayList<>();
        options.add("WAGON");
        options.add("BATEAU");
        options.add("");

        String choix = choisir(
                "Quelle deuxième carte voulez-vous ?",
                options,
                null,
                true);

        for (CarteTransport c : jeu.getCartesTransportVisibles()) {
            nomsCartesTransportVisibles.add(c.getNom());
        }
        options.addAll(nomsCartesTransportVisibles);

        if (choix.equals("WAGON")) {
            log(String.format("%s a choisi choisi de piocher un wagon en deuxième", toLog()));
            this.addCarteTransport(jeu.getPilesDeCartesWagon().piocher());
        } else if (choix.equals("BATEAU")) {
            log(String.format("%s a choisi choisi de piocher un bateau en deuxième", toLog()));
            this.addCarteTransport(jeu.getPilesDeCartesBateau().piocher());
        } else if (nomsCartesTransportVisibles.contains(choix)) {
            CarteTransport carteChoisie = null;
            for (CarteTransport c : jeu.getCartesTransportVisibles()) {
                if (c.getNom().equals(choix)) {
                    carteChoisie = c;
                    cartesTransport.add(carteChoisie);
                    jeu.getCartesTransportVisibles().remove(carteChoisie);
                    break;
                }
                choixCarte();
            }
        }
    }

    public void prendreCarteWagon() {

        List<String> nomsCartesTransportVisibles = new ArrayList<>();

        this.addCarteTransport(jeu.getPilesDeCartesWagon().piocher());

        List<String> options = new ArrayList<>();
        options.add("WAGON");
        options.add("BATEAU");
        options.add("");

        for (CarteTransport c : jeu.getCartesTransportVisibles()) {
            nomsCartesTransportVisibles.add(c.getNom());
        }
        options.addAll(nomsCartesTransportVisibles);

        String choix = choisir(
                "Quelle deuxième carte voulez-vous ?",
                options,
                null,
                true);

        if (choix.equals("WAGON")) {
            log(String.format("%s a choisi choisi de piocher un wagon en deuxième", toLog()));
            this.addCarteTransport(jeu.getPilesDeCartesWagon().piocher());
        } else if (choix.equals("BATEAU")) {
            log(String.format("%s a choisi choisi de piocher un bateau en deuxième", toLog()));
            this.addCarteTransport(jeu.getPilesDeCartesBateau().piocher());
        } else if (nomsCartesTransportVisibles.contains(choix)) {
            CarteTransport carteChoisie = null;
            for (CarteTransport c : jeu.getCartesTransportVisibles()) {
                if (c.getNom().equals(choix)) {
                    carteChoisie = c;
                    cartesTransport.add(carteChoisie);
                    jeu.getCartesTransportVisibles().remove(carteChoisie);
                    break;
                }
                choixCarte();
            }
        }
    }

    public void prendreCarteBateau() {

        List<String> nomsCartesTransportVisibles = new ArrayList<>();

        this.addCarteTransport(jeu.getPilesDeCartesBateau().piocher());

        List<String> options = new ArrayList<>();
        options.add("WAGON");
        options.add("BATEAU");
        options.add("");

        for (CarteTransport c : jeu.getCartesTransportVisibles()) {
            nomsCartesTransportVisibles.add(c.getNom());
        }
        options.addAll(nomsCartesTransportVisibles);

        String choix = choisir(
                "Quelle deuxième carte voulez-vous ?",
                options,
                boutons,
                true);

        if (choix.equals("WAGON")) {
            log(String.format("%s a choisi choisi de piocher un wagon en deuxième", toLog()));
            this.addCarteTransport(jeu.getPilesDeCartesWagon().piocher());
        } else if (choix.equals("BATEAU")) {
            log(String.format("%s a choisi choisi de piocher un bateau en deuxième", toLog()));
            this.addCarteTransport(jeu.getPilesDeCartesBateau().piocher());
        } else if (nomsCartesTransportVisibles.contains(choix)) {
            CarteTransport carteChoisie = null;
            for (CarteTransport c : jeu.getCartesTransportVisibles()) {
                if (c.getNom().equals(choix)) {
                    carteChoisie = c;
                    cartesTransport.add(carteChoisie);
                    jeu.getCartesTransportVisibles().remove(carteChoisie);
                    break;
                }
                choixCarte();
            }
        }
    }

    public void choixCarte() {
        List<String> options = new ArrayList<>();
        options.add("WAGON");
        options.add("BATEAU");


        String choix = choisir(
                "Que voulez-vous replacer (wagon ou bateau) ?",
                options,
                null,
                false);

        if (choix.equals("WAGON")) {
            jeu.getCartesTransportVisibles().add(jeu.piocherCarteWagon());
        } else if (choix.equals("BATEAU")) {
            jeu.getCartesTransportVisibles().add(jeu.piocherCarteBateau());
        }
    }

    private void echangerWagon() {
        List<String> options2 = new ArrayList<>();
        for (int i = 1; i <= nbPionsWagonEnReserve; i++) {
            options2.add(String.valueOf(i));
        }
        String choix2 = choisir(
                "Choisissez le nombre de pions wagon que vous voulez échanger",
                options2,
                null,
                false);
        int nbPionsWagonEchanger = Integer.parseInt(choix2);
        nbPionsWagon += nbPionsWagonEchanger;
        nbPionsWagonEnReserve -= nbPionsWagonEchanger;
        nbPionsBateau -= nbPionsWagonEchanger;
        nbPionsBateauEnReserve += nbPionsWagonEchanger;
        score -= nbPionsWagonEchanger;
        log(String.format("%s a échangé " + nbPionsWagonEchanger + " pions wagon contre " + nbPionsWagonEchanger + " pions bateau", toLog()));
        tours = false;
    }

    private void echangerBateau() {
        List<String> options2 = new ArrayList<>();
        for (int i = 1; i <= nbPionsBateauEnReserve; i++) {
            options2.add(String.valueOf(i));
        }
        String choix2 = choisir(
                "Choisissez le nombre de pions bateau que vous voulez échanger",
                options2,
                null,
                false);
        int nbPionsBateauEchanger = Integer.parseInt(choix2);
        nbPionsBateau += nbPionsBateauEchanger;
        nbPionsBateauEnReserve -= nbPionsBateauEchanger;
        nbPionsWagon -= nbPionsBateauEchanger;
        nbPionsWagonEnReserve += nbPionsBateauEchanger;
        score -= nbPionsBateauEchanger;
        log(String.format("%s a échangé " + nbPionsBateauEchanger + " pions bateau contre " + nbPionsBateauEchanger + " pions wagon", toLog()));
        tours = false;
    }


    //pioche une carte transport visible
    //le joueur clique sur une carte transport visible
    private void piocherCarteTransportVisible() {
        List<String> options = new ArrayList<>();
        for (int i = 0; i < jeu.getCartesTransportVisibles().size(); i++) {
            options.add(String.valueOf(i));
        }
        String choix = choisir(
                "Choisissez la carte transport que vous voulez piocher",
                options,
                null,
                false);

        if (choix.equals("")) {
            return;
        } else {
            CarteTransport carteTransport = jeu.getCartesTransportVisibles().get(Integer.parseInt(choix));
            cartesTransport.add(carteTransport);
            jeu.getCartesTransportVisibles().remove(carteTransport);
        }
    }


    public void capturerRoute() {
        for (Route r : jeu.getRoutesLibres()) {
            if (r.getNom().equals(MainTour)) {
                if (r instanceof RouteTerrestre) {
                    capturerRouteTerrestre((RouteTerrestre) r);

                } else if (r instanceof RouteMaritime) {
                    capturerRouteMaritime((RouteMaritime) r);

                } else if (r instanceof RoutePaire) {
                    capturerRoutePaire((RoutePaire) r);

                }

            }


        }
    }

        public void capturerRouteTerrestre(RouteTerrestre r) {
            int count = 0;
            List<CarteTransport> cartesCorrect = new ArrayList<>();
                for (CarteTransport c : cartesTransport) {

                    if (c.getCouleur().equals(r.getCouleur()) && c.getType().equals(TypeCarteTransport.WAGON)) {
                    count++;
                    cartesCorrect.add(c);

                    }else if (c.getType().equals(TypeCarteTransport.JOKER)){
                        count++;
                        cartesCorrect.add(c);

                    }
                }
                if (count >= r.getLongueur()) {
                List<String> choixCartes = cartesCorrect.stream().map(CarteTransport::getNom).collect(Collectors.toList());
                Couleur finalChosenColor = null;
                while (cartesTransportPosees.size() < r.getLongueur()) {
                    String choix = choisir(
                            "Choisissez les cartes transport que vous voulez poser",
                            choixCartes,
                            null,
                            false);
                    for (CarteTransport c : cartesCorrect) {
                        if (c.getNom().equals(choix) && finalChosenColor == null && !c.getType().equals(TypeCarteTransport.JOKER)) {
                            cartesTransportPosees.add(c);
                            cartesTransport.remove(c);
                            choixCartes.remove(choix);
                            finalChosenColor = c.getCouleur();
                        } else if (c.getNom().equals(choix) && (c.getCouleur().equals(finalChosenColor) || c.getType().equals(TypeCarteTransport.JOKER))) {
                            cartesTransportPosees.add(c);
                            cartesTransport.remove(c);
                            choixCartes.remove(choix);
                        }
                    }

                }
                for (CarteTransport c : cartesTransportPosees) {
                    jeu.getPilesDeCartesWagon().defausser(c);
                    setNbPionsWagon(getNbPionsWagon() - 1);
                }
                score += r.getScore();
                cartesTransportPosees.clear();
                routes.add(r);
                if (jeu.getJoueurs().size()<=3){
                    jeu.getRoutesLibres().remove(r.getRouteParallele());
                }
                jeu.getRoutesLibres().remove(r);
                tours = false;





                }

        }
        public void capturerRouteMaritime(RouteMaritime r) {

        }
        public void capturerRoutePaire(RoutePaire r) {

        }


    //le joueur tire 4 nouvelles destinations et doit en garder au moins une
    public void prendreDestinations() {
        //choix des destinations initiales
        boutons = new ArrayList<>();
        unChoix = new ArrayList<>();
        List<Destination> pileDest = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Destination d = jeu.getPileDestinations().get(0);
            jeu.getPileDestinations().remove(0);
            pileDest.add(d);
            Bouton b;
            boutons.add(b = new Bouton(String.valueOf(jeu.getPileDestinations().get(0)), jeu.getPileDestinations().get(0).getNom()));
            unChoix.add(jeu.getPileDestinations().get(0).getNom());
        }

        unChoix.add("");
        Bouton b;
        b = new Bouton("tout garder", "");
        boutons.add(b);
        String c1 = choisir("Choisissez les destinations à défausser :", unChoix, boutons, true);
        int cpt = 0;
        while (!c1.equals("") && pileDest.size() > 1) {
            String finalC = c1;
            Destination d = pileDest.stream().filter(obj -> obj.getNom().equals(finalC)).findFirst().orElse(null);
            boutons.clear();
            unChoix.clear();
            pileDest.remove(d);
            jeu.getPileDestinations().add(d);
            for (Destination destination : pileDest) {
                unChoix.add(destination.getNom());
                Bouton b2;
                boutons.add(b2 = new Bouton(String.valueOf(destination), destination.getNom()));
            }
            boutons.add(b);
            unChoix.add("");
            cpt++;
            if (cpt != 4) {
                c1 = choisir("Choisissez les destinations à défausser :", unChoix, boutons, true);
            }
        }
        for (Destination destination : pileDest) {
            destinations.add(destination);
        }

        tours = false;

    }

    //le joueur construit un port sur une ville à condition qu'il possède une route qui mène à cette ville
    // pour construire un port le joueur doit dépenser 2 cartes wagons et 2 cartes bateau toutes de la même couleur
    //le joueur écrit le nom de la ville où il veut construire un port
    public void construirePort() {
        List<String> options = new ArrayList<>();
        for (int i = 0; i < jeu.getPortsLibres().size(); i++) {
            options.add(jeu.getPortsLibres().get(i).getNom());
        }
        String choix = choisir(
                "Choisissez la ville où vous voulez construire un port",
                options,
                null,
                false);

        if (choix.equals("")) {
            return;
        } else {
            Ville ville = jeu.getPortsLibres().get(Integer.parseInt(choix));
            if (ville.getNom().equals(choix)) {
                nbPionsWagon -= 2;
                nbPionsBateau -= 2;
                /*if (possedeRoute(ville)) {
                    if (possedeCartesTransport(ville)) {
                        ville.setPort(true);

                    }
                }*/
            }
        }
    }

    public void addPort(Ville ville) {
        ports.add(ville);
    }


    /**
     * Attend une entrée de la part du joueur (au clavier ou sur la websocket) et
     * renvoie le choix du joueur.
     * <p>
     * Cette méthode lit les entrées du jeu (`Jeu.lireligne()`) jusqu'à ce
     * qu'un choix valide (un élément de `choix` ou de `boutons` ou
     * éventuellement la chaîne vide si l'utilisateur est autorisé à passer) soit
     * reçu.
     * Lorsqu'un choix valide est obtenu, il est renvoyé par la fonction.
     * <p>
     * Exemple d'utilisation pour demander à un joueur de répondre à une question
     * par "oui" ou "non" :
     * <p>
     * ```
     * List<String> choix = Arrays.asList("Oui", "Non");
     * String input = choisir("Voulez-vous faire ceci ?", choix, null, false);
     * ```
     * <p>
     * Si par contre on voulait proposer les réponses à l'aide de boutons, on
     * pourrait utiliser :
     * <p>
     * ```
     * List<Bouton> boutons = Arrays.asList(new Bouton("Un", "1"), new Bouton("Deux", "2"), new Bouton("Trois", "3"));
     * String input = choisir("Choisissez un nombre.", null, boutons, false);
     * ```
     *
     * @param instruction message à afficher à l'écran pour indiquer au joueur la
     *                    nature du choix qui est attendu
     * @param choix       une collection de chaînes de caractères correspondant aux
     *                    choix valides attendus du joueur
     * @param boutons     une collection de `Bouton` représentés par deux String (label,
     *                    valeur) correspondant aux choix valides attendus du joueur
     *                    qui doivent être représentés par des boutons sur
     *                    l'interface graphique (le label est affiché sur le bouton,
     *                    la valeur est ce qui est envoyé au jeu quand le bouton est
     *                    cliqué)
     * @param peutPasser  booléen indiquant si le joueur a le droit de passer sans
     *                    faire de choix. S'il est autorisé à passer, c'est la
     *                    chaîne de caractères vide ("") qui signifie qu'il désire
     *                    passer.
     * @return le choix de l'utilisateur (un élement de `choix`, ou la valeur
     * d'un élément de `boutons` ou la chaîne vide)
     */
    public String choisir(

            String instruction,
            Collection<String> choix,
            Collection<Bouton> boutons,
            boolean peutPasser) {
        if (choix == null)
            choix = new ArrayList<>();
        if (boutons == null)
            boutons = new ArrayList<>();

        HashSet<String> choixDistincts = new HashSet<>(choix);
        choixDistincts.addAll(boutons.stream().map(Bouton::valeur).toList());
        if (peutPasser || choixDistincts.isEmpty()) {
            choixDistincts.add("");
        }

        String entree;
        // Lit l'entrée de l'utilisateur jusqu'à obtenir un choix valide
        while (true) {
            jeu.prompt(instruction, boutons, peutPasser);
            entree = jeu.lireLigne();
            // si une réponse valide est obtenue, elle est renvoyée
            if (choixDistincts.contains(entree)) {
                return entree;
            }
        }
    }

    /**
     * Affiche un message dans le log du jeu (visible sur l'interface graphique)
     *
     * @param message le message à afficher (peut contenir des balises html pour la
     *                mise en forme)
     */
    public void log(String message) {
        jeu.log(message);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(String.format("=== %s (%d pts) ===", nom, score));
        joiner.add(String.format("  Wagons: %d  Bateaux: %d", nbPionsWagon, nbPionsBateau));
        return joiner.toString();
    }

    /**
     * @return une chaîne de caractères contenant le nom du joueur, avec des balises
     * HTML pour être mis en forme dans le log
     */
    public String toLog() {
        return String.format("<span class=\"joueur\">%s</span>", nom);
    }

    /**
     * Vérifie si la destination donnée est complétée par le joueur.
     *
     * @param d la destination à vérifier
     * @return true si la destination est complétée, false sinon
     */
    public boolean destinationEstComplete(Destination d) {
        return false;
    }

    public void addCarteTransport(CarteTransport c) {
        cartesTransport.add(c);
    }

    public void addDestination(Destination d) {
        destinations.add(d);
    }

    public int calculerScoreFinal() {
        int scoreFinal = 0;

        //ajout du score actuel
        scoreFinal += score;

        //bonus et malus des cartes destinations

        //score des ports

        //-4 par ports non utilisés

        return scoreFinal;
    }

    /**
     * Renvoie une représentation du joueur sous la forme d'un dictionnaire de
     * valeurs sérialisables
     * (qui sera converti en JSON pour l'envoyer à l'interface graphique)
     */
    Map<String, Object> dataMap() {
        return Map.ofEntries(
                Map.entry("nom", nom),
                Map.entry("couleur", couleur),
                Map.entry("score", score),
                Map.entry("pionsWagon", nbPionsWagon),
                Map.entry("pionsWagonReserve", nbPionsWagonEnReserve),
                Map.entry("pionsBateau", nbPionsBateau),
                Map.entry("pionsBateauReserve", nbPionsBateauEnReserve),
                Map.entry("destinationsIncompletes",
                        destinations.stream().filter(d -> !destinationEstComplete(d)).toList()),
                Map.entry("destinationsCompletes", destinations.stream().filter(this::destinationEstComplete).toList()),
                Map.entry("main", cartesTransport.stream().sorted().toList()),
                Map.entry("inPlay", cartesTransportPosees.stream().sorted().toList()),
                Map.entry("ports", ports.stream().map(Ville::nom).toList()),
                Map.entry("routes", routes.stream().map(Route::getNom).toList()));
    }
}
