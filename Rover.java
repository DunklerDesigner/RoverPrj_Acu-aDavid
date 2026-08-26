import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Representa un Rover utilizado para la exploracion de la superficie marciana.
 *
 * <p>El Rover administra su posicion, potencia disponible, detecciones de fugas
 * de calor, recargas y los mandatos realizados por el controlador humano.
 * Tambien mantiene un registro de los Rovers creados.
 */
public class Rover {

  private String nombreRover;
  private String codigoRover;

  private double potenciaInicial;
  private double potenciaDisponible;

  private int posicionInicialX;
  private int posicionInicialY;
  private int posicionActualX;
  private int posicionActualY;

  private int cantidadRecargasRealizadas;
  private int cantidadDeteccionesCalor;

  private ArrayList<ArrayList<String>> mandatosExitosos;
  private ArrayList<ArrayList<String>> mandatosFallidos;

  private static int cantidadRovers;
  private static ArrayList<Rover> roversCreados = new ArrayList<Rover>();

  private static final double POTENCIA_OMISION = 100.0;
  private static final double GASTO_MOVIMIENTO = 0.5;
  private static final double GASTO_DETECCION = 0.25;
  private static final int MAX_RECARGAS = 5;

  /**
   * Crea un Rover utilizando la cantidad de potencia establecida por omision.
   *
   * @param nombreRover nombre asignado al Rover
   */
  public Rover(String nombreRover) {
    this(nombreRover, POTENCIA_OMISION);
  }

  /**
   * Crea un Rover con un nombre y una cantidad de potencia especifica.
   *
   * El Rover inicia en la posicion (0, 0), sin recargas ni detecciones
   * realizadas y con sus registros de mandatos vacios.
   *
   * @param nombreRover nombre asignado al Rover
   * @param potenciaInicial cantidad inicial de potencia disponible
   */
  public Rover(String nombreRover, double potenciaInicial) {
    this.nombreRover = nombreRover;
    this.codigoRover = "DVD-" + (cantidadRovers + 1);

    this.potenciaInicial = potenciaInicial;
    this.potenciaDisponible = potenciaInicial;

    this.posicionInicialX = 0;
    this.posicionInicialY = 0;
    this.posicionActualX = posicionInicialX;
    this.posicionActualY = posicionInicialY;

    this.cantidadRecargasRealizadas = 0;
    this.cantidadDeteccionesCalor = 0;

    this.mandatosExitosos = new ArrayList<ArrayList<String>>();
    this.mandatosFallidos = new ArrayList<ArrayList<String>>();

    // Registra el nuevo Rover dentro de la informacion compartida de la clase.
    cantidadRovers++;
    roversCreados.add(this);
  }

  /**
   * Intenta desplazar el Rover una posicion hacia arriba.
   *
   * El movimiento solamente se realiza cuando existe potencia suficiente
   * y no se detecta una fuga de calor.
   */
  public void moverArriba() {
    if (validarPotencia()) {
      if (!detectarFugasCalor()) {
        posicionActualY++;
        potenciaDisponible -= GASTO_MOVIMIENTO;
        registrarMandato("Mover arriba", "Exitoso");
      } else {
        registrarMandato("Mover arriba", "No posible: fuga de calor");
      }
    } else {
      registrarMandato("Mover arriba", "No posible: potencia insuficiente");
    }
  }

  /**
   * Intenta desplazar el Rover una posicion hacia abajo.
   *
   * El movimiento solamente se realiza cuando existe potencia suficiente
   * y no se detecta una fuga de calor.
   */
  public void moverAbajo() {
    if (validarPotencia()) {
      if (!detectarFugasCalor()) {
        posicionActualY--;
        potenciaDisponible -= GASTO_MOVIMIENTO;
        registrarMandato("Mover abajo", "Exitoso");
      } else {
        registrarMandato("Mover abajo", "No posible: fuga de calor");
      }
    } else {
      registrarMandato("Mover abajo", "No posible: potencia insuficiente");
    }
  }

  /**
   * Intenta desplazar el Rover una posicion hacia la izquierda.
   *
   * El movimiento solamente se realiza cuando existe potencia suficiente
   * y no se detecta una fuga de calor.
   */
  public void moverIzquierda() {
    if (validarPotencia()) {
      if (!detectarFugasCalor()) {
        posicionActualX--;
        potenciaDisponible -= GASTO_MOVIMIENTO;
        registrarMandato("Mover izquierda", "Exitoso");
      } else {
        registrarMandato("Mover izquierda", "No posible: fuga de calor");
      }
    } else {
      registrarMandato("Mover izquierda", "No posible: potencia insuficiente");
    }
  }

  /**
   * Intenta desplazar el Rover una posicion hacia la derecha.
   *
   * El movimiento solamente se realiza cuando existe potencia suficiente
   * y no se detecta una fuga de calor.
   */
  public void moverDerecha() {
    if (validarPotencia()) {
      if (!detectarFugasCalor()) {
        posicionActualX++;
        potenciaDisponible -= GASTO_MOVIMIENTO;
        registrarMandato("Mover derecha", "Exitoso");
      } else {
        registrarMandato("Mover derecha", "No posible: fuga de calor");
      }
    } else {
      registrarMandato("Mover derecha", "No posible: potencia insuficiente");
    }
  }

  /**
   * Obtiene la posicion actual del Rover.
   *
   * @return posicion actual representada mediante las coordenadas (x, y)
   */
  public String getPosicionActual() {
    return "(" + posicionActualX + ", " + posicionActualY + ")";
  }

  /**
   * Obtiene la cantidad de potencia que posee actualmente el Rover.
   *
   * @return potencia disponible
   */
  public double getPotenciaDisponible() {
    return potenciaDisponible;
  }

  /**
   * Intenta recargar una cantidad determinada de potencia.
   *
   * <p>La recarga solamente puede realizarse mientras el Rover no haya
   * alcanzado el numero maximo de recargas permitidas.
   *
   * @param potencia cantidad de unidades que se desean recargar
   */
  public void recargarPotencia(double potencia) {
    if (validarRecarga()) {
      potenciaDisponible += potencia;
      cantidadRecargasRealizadas++;
      registrarMandato(
          "Recarga (" + potencia + " unidades)",
          "Exitoso"
      );
    } else {
      registrarMandato(
          "Recarga (" + potencia + " unidades)",
          "No posible: recargas agotadas"
      );
    }
  }

  /**
   * Genera una representacion del estado completo del Rover.
   *
   * <p>Incluye informacion general, potencia, posicion, recargas, detecciones
   * y los registros de mandatos exitosos y fallidos.
   *
   * @return informacion completa del Rover
   */
  @Override
  public String toString() {
    String mensaje = "";

    mensaje += "========== Estado del Rover ==========\n";
    mensaje += "Codigo: " + codigoRover + "\n";
    mensaje += "Nombre: " + nombreRover + "\n";
    mensaje += "Potencia inicial: " + potenciaInicial + "\n";
    mensaje += "Potencia disponible: " + potenciaDisponible + "\n";
    mensaje += "Recargas disponibles: "
        + (MAX_RECARGAS - cantidadRecargasRealizadas) + "\n";
    mensaje += "Detecciones de calor realizadas: "
        + cantidadDeteccionesCalor + "\n";
    mensaje += "Posicion inicial: ("
        + posicionInicialX + ", " + posicionInicialY + ")\n";
    mensaje += "Posicion actual: "
        + getPosicionActual() + "\n";

    mensaje += "\nMandatos exitosos:\n";

    if (mandatosExitosos.isEmpty()) {
      mensaje += "Sin mandatos exitosos.\n";
    } else {
      for (ArrayList<String> mandato : mandatosExitosos) {
        mensaje += mandato + "\n";
      }
    }

    mensaje += "\nMandatos fallidos:\n";

    if (mandatosFallidos.isEmpty()) {
      mensaje += "Sin mandatos fallidos.\n";
    } else {
      for (ArrayList<String> mandato : mandatosFallidos) {
        mensaje += mandato + "\n";
      }
    }

    return mensaje;
  }

  /**
   * Obtiene la cantidad total de Rovers que han sido creados.
   *
   * @return cantidad total de Rovers creados
   */
  public static int getCantidadRovers() {
    return cantidadRovers;
  }

  /**
   * Obtiene la informacion de todos los Rovers que han sido creados.
   *
   * @return informacion de todos los Rovers creados
   */
  public static String consultarRoversCreados() {
    String mensaje = "";

    if (roversCreados.isEmpty()) {
      return "No se han creado Rovers.";
    }

    for (Rover rover : roversCreados) {
      mensaje += rover.toString() + "\n";
    }

    return mensaje;
  }

  /**
   * Realiza una deteccion de fuga de calor.
   *
   * <p>Cada deteccion consume potencia y genera un numero aleatorio. Existe
   * una fuga cuando el numero generado es mayor o igual que 0.5.
   *
   * @return true si se detecta una fuga de calor; false en caso contrario
   */
  private boolean detectarFugasCalor() {
    cantidadDeteccionesCalor++;
    potenciaDisponible -= GASTO_DETECCION;

    Random random = new Random();
    double numeroAleatorio = random.nextDouble();

    return numeroAleatorio >= 0.5;
  }

  /**
   * Verifica si existe suficiente potencia para realizar una deteccion y
   * posteriormente un desplazamiento.
   *
   * @return true si existe potencia suficiente; false en caso contrario
   */
  private boolean validarPotencia() {
    return potenciaDisponible >= GASTO_DETECCION + GASTO_MOVIMIENTO;
  }

  /**
   * Verifica si el Rover puede realizar una nueva recarga.
   *
   * @return true si todavia existen recargas disponibles; false en caso contrario
   */
  private boolean validarRecarga() {
    return cantidadRecargasRealizadas < MAX_RECARGAS;
  }

  /**
   * Obtiene la fecha y hora actuales del sistema.
   *
   * @return fecha y hora actuales con formato dd/MM/yy HH:mm:ss
   */
  private String obtenerFechaHoraActual() {
    Date fechaActual = new Date();
    DateFormat formatoFecha = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

    return formatoFecha.format(fechaActual);
  }

  /**
   * Registra un mandato y lo clasifica como exitoso o fallido.
   *
   * <p>Cada mandato almacena la fecha y hora en que fue realizado, su tipo
   * y el estatus obtenido.
   *
   * @param tipoMandato tipo de mandato solicitado al Rover
   * @param estatusMandato resultado obtenido al intentar realizar el mandato
   */
  private void registrarMandato(String tipoMandato, String estatusMandato) {
    ArrayList<String> mandato = new ArrayList<String>();

    mandato.add(obtenerFechaHoraActual());
    mandato.add(tipoMandato);
    mandato.add(estatusMandato);

    if (estatusMandato.equals("Exitoso")) {
      mandatosExitosos.add(mandato);
    } else {
      mandatosFallidos.add(mandato);
    }
  }
}