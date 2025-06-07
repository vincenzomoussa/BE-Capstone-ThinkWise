package it.epicode.service;

import com.github.javafaker.Faker;
import it.epicode.entity.*;
import it.epicode.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DataGeneratorService {

    @Autowired private AulaRepository aulaRepository;
    @Autowired private InsegnanteRepository insegnanteRepository;
    @Autowired private StudenteRepository studenteRepository;
    @Autowired private CorsoRepository corsoRepository;
    @Autowired private SpesaRepository spesaRepository;
    @Autowired private PagamentoRepository pagamentoRepository;
    @Autowired private AppUserRepository appUserRepository;

    private final Faker faker = new Faker(new Locale("it"));
    private final Random random = new Random();

    private static final List<String> GIORNI = List.of("Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì");
    private static final List<String> FASCE_ORARIE = List.of(
            "08:00-10:00", "10:00-12:00", "12:00-14:00", "14:00-16:00", "16:00-18:00", "18:00-20:00"
    );
    private static final List<String> CORSO_TIPI = List.of("Frontend", "Backend", "UX_UI_Design", "Cybersecurity", "Cloud_Computing", "Data_Science");
    private static final Map<String, List<String>> TECNOLOGIE_CORSO = Map.of(
            "Frontend", List.of("React", "Angular", "Vue", "HTML/CSS", "JavaScript", "TypeScript"),
            "Backend", List.of("Java Spring", "Node.js", ".NET", "Python Django", "Express.js", "PHP Laravel"),
            "UX_UI_Design", List.of("Figma", "Adobe XD", "Sketch", "Prototyping", "Wireframing"),
            "Cybersecurity", List.of("Ethical Hacking", "Network Security", "Penetration Testing", "Firewall"),
            "Cloud_Computing", List.of("AWS", "Azure", "Google Cloud", "Docker", "Kubernetes"),
            "Data_Science", List.of("Python Data", "Machine Learning", "R", "Pandas", "TensorFlow")
    );
    private static final Map<String, String> ROMANI = Map.of(
            "Beginner", "I",
            "Junior", "II",
            "Advanced", "III"
    );

    @Transactional
    public String generateAllData(int numAule, int numInsegnanti, int numStudenti, int numCorsi, int numSpese, int numPagamenti) {
        List<Aula> aule = generateAule(numAule);
        List<Insegnante> insegnanti = generateInsegnanti(numInsegnanti);
        List<Studente> studenti = generateStudenti(numStudenti);
        List<Corso> corsi = generateCorsi(numCorsi, insegnanti, studenti, aule);
        generateSpese(numSpese);
        generatePagamenti(studenti);
        return "Dati generati con successo: " + numAule + " Aule, " + numInsegnanti + " Insegnanti, " + numStudenti + " Studenti, " + numCorsi + " Corsi, " + numSpese + " Spese, " + (numStudenti*12) + " Pagamenti.";
    }

    private List<Aula> generateAule(int numAule) {
        List<Aula> aule = new ArrayList<>();
        for (int i = 1; i <= numAule; i++) {
            Aula aula = new Aula();
            aula.setNome("Aula " + i);
            aula.setCapienzaMax(random.nextInt(5) + 1); // 1-5
            aulaRepository.save(aula);
            aule.add(aula);
        }
        return aule;
    }

    private List<Insegnante> generateInsegnanti(int numInsegnanti) {
        List<Insegnante> insegnanti = new ArrayList<>();
        for (int i = 0; i < numInsegnanti; i++) {
            String nome = faker.name().firstName();
            String cognome = faker.name().lastName();
            Insegnante insegnante = new Insegnante();
            insegnante.setNome(nome);
            insegnante.setCognome(cognome);
            insegnante.setEmail((nome + "." + cognome + "@gmail.com").replaceAll("[ '’]", "").toLowerCase());
            insegnante.setEta(faker.number().numberBetween(25, 60));
            insegnante.setDataAssunzione(LocalDate.ofInstant(faker.date().past(5 * 365, TimeUnit.DAYS).toInstant(), ZoneId.systemDefault()));
            // Specializzazioni 1-3
            List<String> specs = new ArrayList<>(CORSO_TIPI);
            Collections.shuffle(specs, random);
            Set<CorsoTipo> specializzazioni = specs.subList(0, random.nextInt(3) + 1).stream().map(CorsoTipo::valueOf).collect(Collectors.toSet());
            insegnante.setSpecializzazioni(specializzazioni);
            // Giorni disponibili
            List<String> giorni = new ArrayList<>(GIORNI);
            Collections.shuffle(giorni, random);
            Set<String> giorniDisponibili = new HashSet<>(giorni.subList(0, random.nextInt(GIORNI.size()) + 1));
            insegnante.setGiorniDisponibili(giorniDisponibili);
            // Fasce orarie disponibili
            List<String> orari = new ArrayList<>(FASCE_ORARIE);
            Collections.shuffle(orari, random);
            Set<String> fasceOrarieDisponibili = new HashSet<>(orari.subList(0, random.nextInt(FASCE_ORARIE.size()) + 1));
            insegnante.setFasceOrarieDisponibili(fasceOrarieDisponibili);
            insegnanteRepository.save(insegnante);
            insegnanti.add(insegnante);
        }
        return insegnanti;
    }

    private List<Studente> generateStudenti(int numStudenti) {
        List<Studente> studenti = new ArrayList<>();
        for (int i = 0; i < numStudenti; i++) {
            String nome = faker.name().firstName();
            String cognome = faker.name().lastName();
            Studente studente = new Studente();
            studente.setNome(nome);
            studente.setCognome(cognome);
            studente.setEmail((nome + "." + cognome + "@gmail.com").replaceAll("[ '’]", "").toLowerCase());
            studente.setEta(faker.number().numberBetween(18, 40));
            studente.setDataIscrizione(LocalDate.ofInstant(faker.date().past(2 * 365, TimeUnit.DAYS).toInstant(), ZoneId.systemDefault()));
            // Preferenze corso 1-3
            List<String> prefs = new ArrayList<>(CORSO_TIPI);
            Collections.shuffle(prefs, random);
            Set<CorsoTipo> preferenze = prefs.subList(0, random.nextInt(3) + 1).stream().map(CorsoTipo::valueOf).collect(Collectors.toSet());
            studente.setPreferenzaCorso(preferenze);
            // Giorni preferiti
            List<String> giorni = new ArrayList<>(GIORNI);
            Collections.shuffle(giorni, random);
            Set<String> giorniPreferiti = new HashSet<>(giorni.subList(0, random.nextInt(GIORNI.size()) + 1));
            studente.setGiorniPreferiti(giorniPreferiti);
            // Fasce orarie preferite
            List<String> orari = new ArrayList<>(FASCE_ORARIE);
            Collections.shuffle(orari, random);
            Set<String> fasceOrariePreferite = new HashSet<>(orari.subList(0, random.nextInt(FASCE_ORARIE.size()) + 1));
            studente.setFasceOrariePreferite(fasceOrariePreferite);
            studenteRepository.save(studente);
            studenti.add(studente);
        }
        return studenti;
    }

    private List<Corso> generateCorsi(int numCorsi, List<Insegnante> insegnanti, List<Studente> studenti, List<Aula> aule) {
        List<Corso> corsi = new ArrayList<>();
        for (int i = 0; i < numCorsi; i++) {
            Corso corso = new Corso();
            // Tipo corso e tecnologia coerente
            String corsoTipo = CORSO_TIPI.get(random.nextInt(CORSO_TIPI.size()));
            List<String> tecnologie = TECNOLOGIE_CORSO.get(corsoTipo);
            String tecnologia = tecnologie.get(random.nextInt(tecnologie.size()));
            // Livello
            String[] livelli = {"Beginner", "Junior", "Advanced"};
            String livello = livelli[random.nextInt(livelli.length)];
            // Nome corso coerente
            String nomeCorso = tecnologia + " " + ROMANI.get(livello);
            corso.setNome(nomeCorso);
            corso.setTipoCorso(TipoCorso.values()[random.nextInt(TipoCorso.values().length)]);
            corso.setCorsoTipo(CorsoTipo.valueOf(corsoTipo));
            corso.setLivello(Livello.valueOf(livello));
            // Frequenza, giorno, orario
            String frequenza = random.nextBoolean() ? "1 volta a settimana" : "2 volte a settimana";
            corso.setFrequenza(frequenza);
            String giorno = GIORNI.get(random.nextInt(GIORNI.size()));
            corso.setGiorno(giorno);
            String orario = FASCE_ORARIE.get(random.nextInt(FASCE_ORARIE.size()));
            corso.setOrario(orario);
            if (frequenza.equals("2 volte a settimana")) {
                String secondoGiorno = GIORNI.get(random.nextInt(GIORNI.size()));
                String secondoOrario = FASCE_ORARIE.get(random.nextInt(FASCE_ORARIE.size()));
                corso.setSecondoGiorno(secondoGiorno);
                corso.setSecondoOrario(secondoOrario);
            }
            // Aula e capienza
            Aula aula = aule.get(random.nextInt(aule.size()));
            corso.setAula(aula);
            // Insegnante compatibile
            List<Insegnante> compatibili = insegnanti.stream().filter(ins -> ins.getSpecializzazioni().contains(CorsoTipo.valueOf(corsoTipo))).collect(Collectors.toList());
            if (compatibili.isEmpty()) compatibili = insegnanti;
            corso.setInsegnante(compatibili.get(random.nextInt(compatibili.size())));
            // Attivo
            corso.setAttivo(true);
            // Studenti compatibili (preferenze, giorno, orario)
            List<Studente> compatibiliStudenti = studenti.stream().filter(s ->
                s.getPreferenzaCorso().contains(CorsoTipo.valueOf(corsoTipo)) &&
                (s.getGiorniPreferiti().contains(giorno) || (corso.getSecondoGiorno() != null && s.getGiorniPreferiti().contains(corso.getSecondoGiorno()))) &&
                (s.getFasceOrariePreferite().contains(orario) || (corso.getSecondoOrario() != null && s.getFasceOrariePreferite().contains(corso.getSecondoOrario())))
            ).collect(Collectors.toList());
            Collections.shuffle(compatibiliStudenti, random);
            int maxStudenti = Math.min(aula.getCapienzaMax(), compatibiliStudenti.size());
            int numStudentiCorso = maxStudenti == 0 ? 0 : random.nextInt(maxStudenti) + 1;
            corso.setStudenti(compatibiliStudenti.subList(0, numStudentiCorso));
            corsoRepository.save(corso);
            corsi.add(corso);
        }
        return corsi;
    }

    private void generateSpese(int numSpese) {
        List<Spesa.CategoriaSpesa> categorie = List.of(Spesa.CategoriaSpesa.values());
        Map<Spesa.CategoriaSpesa, List<String>> descrizioni = Map.of(
            Spesa.CategoriaSpesa.PERSONALE, List.of("Personale ATA", "Stipendi degli Insegnanti", "Stipendio Autisti"),
            Spesa.CategoriaSpesa.MANUTENZIONE, List.of("Mantenimento infrastruttura", "Assistenza tecnica"),
            Spesa.CategoriaSpesa.FORMAZIONE, List.of("Corsi di formazione", "Corsi di aggiornamento"),
            Spesa.CategoriaSpesa.ASSICURAZIONE, List.of("Assicurazione studenti", "Assicurazione personale"),
            Spesa.CategoriaSpesa.ATTREZZATURE, List.of("Rata prestito attrezzature", "Acquisto nuovi computer"),
            Spesa.CategoriaSpesa.TRASPORTO, List.of("Mantenimento mezzi di trasporto", "Rimborso spese viaggio"),
            Spesa.CategoriaSpesa.ALTRO, List.of("Spesa generica")
        );
        for (int i = 0; i < numSpese; i++) {
            Spesa spesa = new Spesa();
            Spesa.CategoriaSpesa categoria = categorie.get(random.nextInt(categorie.size()));
            spesa.setCategoria(categoria);
            double importo = random.nextInt(3000) + 1; // 1-1000
            if (random.nextBoolean()) importo += 0.5;
            spesa.setImporto(importo);
            spesa.setDataSpesa(LocalDate.ofInstant(faker.date().past(1 * 365, TimeUnit.DAYS).toInstant(), ZoneId.systemDefault()));
            List<String> descPossibili = descrizioni.getOrDefault(categoria, List.of("Spesa generica"));
            spesa.setDescrizione(descPossibili.get(random.nextInt(descPossibili.size())));
            spesaRepository.save(spesa);
        }
    }

    private void generatePagamenti(List<Studente> studenti) {
        List<MetodoPagamento> metodi = List.of(MetodoPagamento.values());
        List<String> mensilita = List.of("Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre");
        int currentYear = LocalDate.now().getYear();
        double totaleSpese = spesaRepository.findAll().stream().mapToDouble(Spesa::getImporto).sum();
        double totalePagamenti = 0;
        for (Studente studente : studenti) {
            for (String mese : mensilita) {
                Pagamento pagamento = new Pagamento();
                pagamento.setStudente(studente);
                pagamento.setImporto(150.0);
                pagamento.setDataPagamento(LocalDate.of(currentYear, mensilita.indexOf(mese) + 1, random.nextInt(28) + 1));
                pagamento.setMensilitaSaldata(mese + " " + currentYear);
                pagamento.setNumeroRicevuta(faker.number().digits(8));
                pagamento.setMetodoPagamento(metodi.get(random.nextInt(metodi.size())));
                pagamento.setNote("Pagamento mensile corso");
                pagamentoRepository.save(pagamento);
                totalePagamenti += 150.0;
            }
        }
        // Se necessario, aggiungi pagamenti extra per garantire plusvalenza
        while (totalePagamenti <= totaleSpese) {
            Pagamento extra = new Pagamento();
            Studente studente = studenti.get(random.nextInt(studenti.size()));
            extra.setStudente(studente);
            extra.setImporto(150.0);
            extra.setDataPagamento(LocalDate.now());
            extra.setMensilitaSaldata("Extra " + (int)(Math.random()*1000));
            extra.setNumeroRicevuta(faker.number().digits(8));
            extra.setMetodoPagamento(metodi.get(random.nextInt(metodi.size())));
            extra.setNote("Pagamento extra per plusvalenza");
            pagamentoRepository.save(extra);
            totalePagamenti += 150.0;
        }
    }
} 