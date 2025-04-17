package com.unse.proyecto.ubicua.principal.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.unse.proyecto.ubicua.principal.database.BDGestor;
import com.unse.proyecto.ubicua.principal.database.DBManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class Utils {

    //Constantes para las BD
    public static final String STRING_TYPE = "text";
    public static final String INT_TYPE = "integer";
    public static final String FLOAT_TYPE = "float";
    public static final String NULL_TYPE = "not null";
    public static final String NULL_YES_TYPE = "null";
    public static final String AUTO_INCREMENT = "auto_increment";
    //Constantes de preferencias
    public static final String IS_FIRST_TIME_LAUNCH = "is_first";
    public static final String USER_ID = "USER_ID";

    public static final String PREF_NAME = "comedor";
    public static final String IS_LOGIN = "login_yes";
    //Constantes para activities
    public static final String USER_INFO = "user_info";
    public static final String OBJ_INFO = "obj_info";
    public static final String FOUND_MODE = "found_mode";
    public static final int OPEN_DISCOVER = 1010;
    //Constante para Volley
    public static final int MY_DEFAULT_TIMEOUT = 15000;

    public static final String[] facultad = {"FAyA", "FCEyT", "FCF", "FCM", "FHCSyS"};
    public static String[] faya = {"Ingeniería Agronómica", "Ingeniería en Alimentos", "Licenciatura en Biotecnología",
            "Licenciatura en Química", "Profesorado en Química", "Tecnicatura en Apicultura"};
    public static String[] fceyt = {"Ingeniería Civil", "Ingeniería Electromecánica", "Ingeniería Electrónica",
            "Ingeniería Eléctrica", "Ingeniería en Agrimensura", "Ingeniería Hidráulica",
            "Ingeniería Industrial", "Ingeniería Vial", "Licenciatura en Hidrología Subterránea",
            "Licenciatura en Matemática", "Licenciatura en Sistemas de Información",
            "Profesorado en Física", "Profesorado en Informática", "Profesorado en Matemática",
            "Programador Universitario en Informática", "Tecnicatura Universitaria Vial",
            "Tecnicatura Universitaria en Construcciones",
            "Tecnicatura Universitaria en Hidrología Subterránea",
            "Tecnicatura Universitaria en Organización y Control de la Producción"};
    public static String[] fcf = {"Ingeniería Forestal", "Ingeniería en Industrias Forestales",
            "Licenciatura en Ecología y Conservación del Ambiente",
            "Tecnicatura Universitaria Fitosanitarista",
            "Tecnicatura Universitaria en Viveros y Plantaciones Forestales",
            "Tecnicatura Universitaria en Aserraderos y Carpintería Industrial"};

    public static String[] fcm = {"Medicina"};

    public static String[] fhcys = {"Licenciatura en Administración", "Contador Público Nacional",
            "Licenciatura en Letras", "Licenciatura en Sociología", "Licenciatura en Enfermería",
            "Licenciatura en Educación para la Salud", "Licenciatura en Obstetricia",
            "Licenciatura en Filosofía", "Licenciatura en Trabajo Social",
            "Licenciatura en Periodismo", "Profesorado en Educación para la Salud",
            "Tecnicatura Sup. Adm. y Gestión Universitaria",
            "Tecnicatura en Educación Intercultural Bilingue"};

    public static void unzip(String zipFile, String targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = new File(targetDirectory, zipEntry.getName());
            if (zipEntry.isDirectory()) {
                newFile.mkdirs();
            } else {
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zis.closeEntry();
            zipEntry = zis.getNextEntry();
        }
        zis.close();
    }


    public static void changeColorDrawable(ImageView view, Context context, int color) {
        DrawableCompat.setTint(
                DrawableCompat.wrap(view.getDrawable()),
                ContextCompat.getColor(context, color));
    }

    /**
     * Método que permite inicializar la BD local
     *
     * @param c Contexto actual de la actividad desde donde de lo convoca
     */
    public static void initBD(Context c) {
        BDGestor gestor = new BDGestor(c);
        DBManager.initializeInstance(gestor);
    }


    public static void showToast(Context c, String msj) {
        Toast.makeText(c, msj, Toast.LENGTH_LONG).show();
    }

    public static void showLog(String msj) {
        Log.e("GPS", msj);
    }


    public static Bitmap resize(Bitmap bitmapToScale, float newWidth, float newHeight) {
        if (bitmapToScale == null)
            return null;
        int width = bitmapToScale.getWidth();
        int height = bitmapToScale.getHeight();

        Matrix matrix = new Matrix();

        matrix.postScale(newWidth / width, newHeight / height);

        return Bitmap.createBitmap(bitmapToScale, 0, 0, bitmapToScale.getWidth(),
                bitmapToScale.getHeight(), matrix, true);
    }


    //Metodo para saber si un permiso esta autorizado o no
    public static boolean isPermissionGranted(Context ctx, String permision) {
        int permission = ActivityCompat.checkSelfPermission(
                ctx,
                permision);
        return permission == PackageManager.PERMISSION_GRANTED;

    }


    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String getAppName(Context context, ComponentName name) {
        try {
            ActivityInfo activityInfo = context.getPackageManager().getActivityInfo(
                    name, PackageManager.GET_META_DATA);
            return activityInfo.loadLabel(context.getPackageManager()).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static Date getFechaDate(String fecha) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaI = null;
        if (fecha != null)
            try {
                fechaI = simpleDateFormat.parse(fecha);

            } catch (ParseException e) {
                simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    fechaI = simpleDateFormat.parse(fecha);

                } catch (ParseException e2) {
                    e2.printStackTrace();
                }
            }

        return fechaI;

    }

    public static Date getFechaDateDMA(String fecha) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date fechaI = null;
        if (fecha != null)
            try {
                fechaI = simpleDateFormat.parse(fecha);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        return fechaI;

    }

    public static Date getFechaDateWithHour(String fecha) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fechaI = null;
        if (fecha != null) {
            try {
                fechaI = simpleDateFormat.parse(fecha);

            } catch (ParseException e) {
                simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                try {
                    fechaI = simpleDateFormat.parse(fecha);

                } catch (ParseException e2) {
                    e2.printStackTrace();
                }
            }
            return fechaI;
        } else return null;


    }

    public static String getHora(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String value = "";
        if (cal.get(Calendar.MINUTE) <= 9) {
            value = cal.get(Calendar.HOUR_OF_DAY) + ":0" +
                    cal.get(Calendar.MINUTE);
        } else {
            value = cal.get(Calendar.HOUR_OF_DAY) + ":" +
                    cal.get(Calendar.MINUTE);
        }

        return value;
    }

    public static String getHoraWithSeconds(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String value = "";
        if (cal.get(Calendar.MINUTE) <= 9) {
            value = cal.get(Calendar.HOUR_OF_DAY) + ":0" +
                    cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
        } else {
            value = cal.get(Calendar.HOUR_OF_DAY) + ":" +
                    cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
        }

        return value;
    }

    public static String getFechaName(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String mesS, diaS, minutosS, segS, horasS;
        int mes = cal.get(Calendar.MONTH) + 1;
        if (mes < 10) {
            mesS = "0" + mes;
        } else
            mesS = String.valueOf(mes);

        int dia = cal.get(Calendar.DAY_OF_MONTH);
        if (dia < 10) {
            diaS = "0" + dia;
        } else
            diaS = String.valueOf(dia);

        int minutos = cal.get(Calendar.MINUTE);
        if (minutos < 10) {
            minutosS = "0" + minutos;
        } else
            minutosS = String.valueOf(minutos);

        int seg = cal.get(Calendar.SECOND);
        if (seg < 10) {
            segS = "0" + seg;
        } else
            segS = String.valueOf(seg);

        int horas = cal.get(Calendar.HOUR_OF_DAY);
        if (horas < 10)
            horasS = "0" + horas;
        else
            horasS = String.valueOf(horas);

        String value = cal.get(Calendar.YEAR) + "-" + mesS + "-"
                + diaS + " " + horasS + ":" +
                minutosS + ":" + segS;

        return value;
    }

    public static String getFechaOrder(Date date) {

        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
            String mesS, diaS, minutosS, segS, horasS;
            int mes = cal.get(Calendar.MONTH) + 1;
            if (mes < 10) {
                mesS = "0" + mes;
            } else
                mesS = String.valueOf(mes);

            int dia = cal.get(Calendar.DAY_OF_MONTH);
            if (dia < 10) {
                diaS = "0" + dia;
            } else
                diaS = String.valueOf(dia);

            int minutos = cal.get(Calendar.MINUTE);
            if (minutos < 10) {
                minutosS = "0" + minutos;
            } else
                minutosS = String.valueOf(minutos);

            int seg = cal.get(Calendar.SECOND);
            if (seg < 10) {
                segS = "0" + seg;
            } else
                segS = String.valueOf(seg);

            int horas = cal.get(Calendar.HOUR_OF_DAY);
            if (horas < 10)
                horasS = "0" + horas;
            else
                horasS = String.valueOf(horas);

            return String.format("%s/%s/%s - %s:%s:%s", diaS, mesS, cal.get(Calendar.YEAR), horasS, minutosS, segS);
        } else return "NO FECHA";

    }

    public static String getBirthday(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String mesS, diaS;
        int mes = cal.get(Calendar.MONTH) + 1;
        if (mes < 10) {
            mesS = "0" + mes;
        } else
            mesS = String.valueOf(mes);

        int dia = cal.get(Calendar.DAY_OF_MONTH);
        if (dia < 10) {
            diaS = "0" + dia;
        } else
            diaS = String.valueOf(dia);
        String value = diaS + "/" + mesS + "/" + cal.get(Calendar.YEAR);

        return value;
    }

    public static String getFechaNameWithinHour(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String mesS, diaS;
        int mes = cal.get(Calendar.MONTH) + 1;
        if (mes < 10) {
            mesS = "0" + mes;
        } else
            mesS = String.valueOf(mes);

        int dia = cal.get(Calendar.DAY_OF_MONTH);
        if (dia < 10) {
            diaS = "0" + dia;
        } else
            diaS = String.valueOf(dia);
        String value = cal.get(Calendar.YEAR) + "-" + mesS + "-"
                + diaS;

        return value;
    }

    public static String getInfoDate(int dia, int mes, int anio) {
        Date fecha = getFechaDateDMA(String.format("%02d-%02d-%02d", dia, mes, anio));
        if (fecha != null) {
            String diaSemana = getDayWeek(fecha);
            return String.format("%s %02d", diaSemana, dia);
        }
        return "";

    }

    public static String getDate(int dia, int mes, int anio) {
        Date fecha = getFechaDateDMA(String.format("%02d-%02d-%02d", dia, mes, anio));
        return getBirthday(fecha);

    }

    public static String getDayWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return "Lunes";
            case Calendar.TUESDAY:
                return "Martes";
            case Calendar.WEDNESDAY:
                return "Miércoles";
            case Calendar.THURSDAY:
                return "Jueves";
            case Calendar.FRIDAY:
                return "Viernes";
            case Calendar.SATURDAY:
                return "Sábado";
            case Calendar.SUNDAY:
                return "Domingo";
        }
        return "";
    }

    public static String getMonth(int mont) {
        /*Date date = new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, mont-1);
        String mes = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        char in = mes.charAt(0);

        return String.valueOf(in).toUpperCase() + mes.substring(1);*/
        switch (mont) {
            case 1:
                return "Enero";
            case 2:
                return "Febrero";
            case 3:
                return "Marzo";
            case 4:
                return "Abril";
            case 5:
                return "Mayo";
            case 6:
                return "Junio";
            case 7:
                return "Julio";
            case 8:
                return "Agosto";
            case 9:
                return "Septiembre";
            case 10:
                return "Octubre";
            case 11:
                return "Noviembre";
            case 12:
                return "Diciembre";
            default:
                return "Desconocido";
        }
    }

    public static String getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String mes = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        char in = mes.charAt(0);

        return in + mes.substring(1);
    }

    public static boolean isDateHabilited(Calendar calendar) {

        if (getDayWeek(calendar.getTime()).equals("Sábado") || getDayWeek(calendar.getTime()).equals("Domingo")) {
            return true;
        }
        return false;
    }

    public static int getEdad(Date fechaNac) {
        Date hoy = new Date(System.currentTimeMillis());
        long tiempo = hoy.getTime() - fechaNac.getTime();
        double years = tiempo / 3.15576e+10;
        int age = (int) Math.floor(years);
        return age;
    }

    public static void changeColor(Drawable drawable, Context mContext, int colorNo) {
        if (drawable instanceof ShapeDrawable)
            ((ShapeDrawable) drawable).getPaint().setColor(ContextCompat.getColor(mContext, colorNo));
        else if (drawable instanceof GradientDrawable)
            ((GradientDrawable) drawable).setColor(ContextCompat.getColor(mContext, colorNo));
        else if (drawable instanceof ColorDrawable)
            ((ColorDrawable) drawable).setColor(ContextCompat.getColor(mContext, colorNo));
    }


    public static String limpiarAcentos(String cadena) {
        String limpio = null;
        if (cadena != null) {
            String valor = cadena;
            valor = valor.toUpperCase();
            // Normalizar texto para eliminar acentos, dieresis, cedillas y tildes
            limpio = Normalizer.normalize(valor, Normalizer.Form.NFD);
            // Quitar caracteres no ASCII excepto la enie, interrogacion que abre, exclamacion que abre, grados, U con dieresis.
            limpio = limpio.replaceAll("[^\\p{ASCII}(N\u0303)(n\u0303)(\u00A1)(\u00BF)(\u00B0)(U\u0308)(u\u0308)]", "");
            // Regresar a la forma compuesta, para poder comparar la enie con la tabla de valores
            limpio = Normalizer.normalize(limpio, Normalizer.Form.NFC);
        }
        return limpio;
    }


    public static String getFechaFormat(String fechaRegistro) {
        Calendar calendar = new GregorianCalendar();
        Date date = getFechaDateWithHour(fechaRegistro);
        if (date != null) {
            calendar.setTime(date);
            String dia = getDayWeek(date);
            String mes = getMonth(date);
            String anio = String.valueOf(calendar.get(Calendar.YEAR));
            String minutosS, segS, horasS;
            int minutos = calendar.get(Calendar.MINUTE);
            if (minutos < 10) {
                minutosS = "0" + minutos;
            } else
                minutosS = String.valueOf(minutos);

            int seg = calendar.get(Calendar.SECOND);
            if (seg < 10) {
                segS = "0" + seg;
            } else
                segS = String.valueOf(seg);

            int horas = calendar.get(Calendar.HOUR_OF_DAY);
            if (horas < 10)
                horasS = "0" + horas;
            else {
                horasS = String.valueOf(horas);
            }
            String hora = String.format("%s:%s:%s", horasS, minutosS, segS);
            String fecha = String.format("%s, %s de %s de %s - %s", dia, calendar.get(Calendar.DAY_OF_MONTH), mes, anio, hora);
            return fecha;
        }
        return "NO FECHA";

    }


}

