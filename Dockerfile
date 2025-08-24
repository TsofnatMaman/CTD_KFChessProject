# השתמש בתמונת בסיס רשמית של Java (גרסת JRE 17)
FROM eclipse-temurin:17-jre-focal

# הגדר את ארגומנט ה-JAR כדי שיהיה קל יותר לעדכן
# החלף את "server/target/kfchess-server.jar" בשם הקובץ האמיתי והמלא של השרת שלך
# הנתיב "server/target/" מתבסס על ההנחה שה-Dockerfile נמצא בתיקיית השורש של הפרויקט הראשי
# ושהשרת שלך נמצא בתיקיית "server" בתוכו.
# החלף את הנתיב הישן בשם הקובץ האמיתי והמלא של השרת
ARG JAR_FILE=server/target/chess-websocket-server-1.0.0-shaded.jar
# העתק את קובץ ה-JAR לתוך הקונטיינר
COPY ${JAR_FILE} app.jar

# הגדר את הפורט בו היישום יאזין
# ב-Render, המשתנה הסביבתי PORT מוגדר אוטומטית. אם השרת שלך מאזין לפורט קבוע (לדוגמה 8080),
# שנה את EXPOSE ל-EXPOSE 8080 וודא שהשרת שלך משתמש בפורט זה.
EXPOSE $PORT

# הפקודה שתופעל עם התחלת הקונטיינר
ENTRYPOINT ["java", "-jar", "/app.jar"]