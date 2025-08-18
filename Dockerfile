# --- שלב 1: Build עם Maven ---
FROM maven:3.9.3-eclipse-temurin-20 AS build

# הגדרת תיקיית עבודה בתוך הקונטיינר
WORKDIR /app

# העתקת כל הפרויקט (parent + server + client)
COPY . .

# בנייה של ה-server (או של כל המודולים במידה ויש multi-module)
RUN mvn clean package -DskipTests

# --- שלב 2: Runtime ---
FROM eclipse-temurin:20-jre-alpine

# הגדרת תיקייה לריצה
WORKDIR /app

# העתקת קובץ ה-jar שנוצר משלב הבנייה
# נניח שה-server מייצר target/server.jar
COPY --from=build /app/server/target/server.jar ./server.jar

# אם צריך גם את ה-client:
# COPY --from=build /app/client/target/client.jar ./client.jar

# חשיפת פורט (למשל 8080 אם זה WebSocket)
EXPOSE 8080

# פקודת ריצה
CMD ["java", "-jar", "server.jar"]
