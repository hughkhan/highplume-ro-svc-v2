del "Encryption.java"
del "Analysis.java"
del "Util.java"


mklink /H "Encryption.java" "../../../../../../../highplume-rw-svc-v2/src/main/java/com/highplume/service/Encryption.java"
mklink /H "Analysis.java" "../../../../../../../highplume-rw-svc-v2/src/main/java/com/highplume/service/Analysis.java"
mklink /H "Util.java" "../../../../../../../highplume-rw-svc-v2/src/main/java/com/highplume/service/Util.java"


fsutil hardlink list "Encryption.java"
fsutil hardlink list "Analysis.java"
fsutil hardlink list "Util.java"

