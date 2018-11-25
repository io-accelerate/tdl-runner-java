#!/usr/bin/env bash

set -e
set -u
set -o pipefail

##############################################################################
##
##  App start up script for UN*X
##
##############################################################################

warn ( ) {
    echo "$*"
}

die ( ) {
    echo
    echo "$*"
    echo
    exit 1
}

# Split up the JVM_OPTS And RECORD_OPTS values into an array, following the shell quoting and substitution rules
function splitJvmOpts() {
    JVM_OPTS=("$@")
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
OSName=linux
case "`uname`" in
  CYGWIN* )
    cygwin=true
    OSName=windows
    ;;
  Darwin* )
    darwin=true
    OSName=macos
    ;;
  MINGW* )
    OSName=windows
    msys=true
    ;;
esac

# Add default JVM options here. You can also use JAVA_OPTS and RECORD_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS=""

APP_NAME="RecordAndUpload"
APP_BASE_NAME=`basename "$0"`

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

# For Cygwin, ensure paths are in UNIX format before anything is touched.
if ${cygwin} ; then
    [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
fi

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/" >&-
APP_HOME="`pwd -P`"
cd "$SAVED" >&-

startRecorderViaCapsule() {
    # Determine the Java command to use to start the JVM.
    if [ -n "$JAVA_HOME" ] ; then
        if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
            # IBM's JDK on AIX uses strange locations for the executables
            JAVACMD="$JAVA_HOME/jre/sh/java"
        else
            JAVACMD="$JAVA_HOME/bin/java"
        fi
        if [ ! -x "$JAVACMD" ] ; then
            die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

    Please set the JAVA_HOME variable in your environment to match the
    location of your Java installation."
        fi
    else
        JAVACMD="java"
        which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

    Please set the JAVA_HOME variable in your environment to match the
    location of your Java installation."
    fi

    # Increase the maximum file descriptors if we can.
    if [ "$cygwin" = "false" -a "$darwin" = "false" ] ; then
        MAX_FD_LIMIT=`ulimit -H -n`
        if [ $? -eq 0 ] ; then
            if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
                MAX_FD="$MAX_FD_LIMIT"
            fi
            ulimit -n $MAX_FD
            if [ $? -ne 0 ] ; then
                warn "Could not set maximum file descriptor limit: $MAX_FD"
            fi
        else
            warn "Could not query maximum file descriptor limit: $MAX_FD_LIMIT"
        fi
    fi

    # For Darwin, add options to specify how the application appears in the dock
    if $darwin; then
        RECORD_OPTS="$RECORD_OPTS \"-Xdock:name=$APP_NAME\" \"-Xdock:icon=$APP_HOME/media/gradle.icns\""
    fi

    # For Cygwin, switch paths to Windows format before running java
    if $cygwin ; then
        APP_HOME=`cygpath --path --mixed "$APP_HOME"`

        # We build the pattern for arguments to be converted via cygpath
        ROOTDIRSRAW=`find -L / -maxdepth 1 -mindepth 1 -type d 2>/dev/null`
        SEP=""
        for dir in $ROOTDIRSRAW ; do
            ROOTDIRS="$ROOTDIRS$SEP$dir"
            SEP="|"
        done
        OURCYGPATTERN="(^($ROOTDIRS))"
        # Add a user-defined pattern to the cygpath arguments
        if [ "$RECORD_CYGPATTERN" != "" ] ; then
            OURCYGPATTERN="$OURCYGPATTERN|($RECORD_CYGPATTERN)"
        fi
        # Now convert the arguments - kludge to limit ourselves to /bin/sh
        i=0
        for arg in "$@" ; do
            CHECK=`echo "$arg"|egrep -c "$OURCYGPATTERN" -`
            CHECK2=`echo "$arg"|egrep -c "^-"`                                 ### Determine if an option

            if [ $CHECK -ne 0 ] && [ $CHECK2 -eq 0 ] ; then                    ### Added a condition
                eval `echo args$i`=`cygpath --path --ignore --mixed "$arg"`
            else
                eval `echo args$i`="\"$arg\""
            fi
            i=$((i+1))
        done
        case $i in
            (0) set -- ;;
            (1) set -- "$args0" ;;
            (2) set -- "$args0" "$args1" ;;
            (3) set -- "$args0" "$args1" "$args2" ;;
            (4) set -- "$args0" "$args1" "$args2" "$args3" ;;
            (5) set -- "$args0" "$args1" "$args2" "$args3" "$args4" ;;
            (6) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" ;;
            (7) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" ;;
            (8) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" ;;
            (9) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" "$args8" ;;
        esac
    fi

    # Prepare params
    PARAM_CONFIG_FILE="--config ${APP_HOME}/config/credentials.config"
    PARAM_STORE_DIR="--store ${APP_HOME}/record/localstore"
    PARAM_SOURCECODE_DIR="--sourcecode ${APP_HOME}"

    # Prepare jar file
    JARFILE=${APP_HOME}/record/record-and-upload-capsule.jar
    if $cygwin ; then
        JARFILE=`cygpath --path --mixed "$JARFILE"`
    fi

    # Look for tokens "9, "10, "11 or "12 in the Java version string,
    # if found, return the whole Java String, otherwise return empty string
    #
    # For e.g.
    #   --version command output    JAVA_VERSION
    #   ========================   ==============
    #   java version "1.8.0_172"     <no output>
    #   java version "9.0.1"            "9
    #   java version "10.0.1"           "10
    #   java version "11.0.2"           "11
    #   java version "12-ea"            "12-ea
    JAVA_VERSION_STRING=$($JAVACMD -version 2>&1)
    echo ${JAVA_VERSION_STRING}
    JAVA_FULL_VERSION=$(echo ${JAVA_VERSION_STRING} | grep -o '\".*\"')
    echo JAVA_FULL_VERSION=${JAVA_FULL_VERSION}
    JAVA_VERSION=$(echo ${JAVA_FULL_VERSION}| grep '"9\|"10\|"11\|"12' || true)

    # Only include digits from value in JAVA_VERSION,
    # If the above value contains non-numeric values,
    # only the digits before it are returned
    #
    # For e.g.
    #   JAVA_VERSION   JAVA_VERSION_INT_VALUE
    #   ============   ======================
    #    <no output>         <no output>
    #         9                  9
    #        10                  10
    #        11                  11
    #       12-ea                12
    #
    # The for loop below iterates through tokens,
    # tokens are separated by the delimeter provided to 'delim'
    # Only the first token is retained, and as pattern the above table,
    # it will always be empty or a numeric value between 9 and 12 (included)
    JAVA_VERSION_INT_VALUE=$(echo $JAVA_VERSION | grep -o '"[0-9]*' | tr -d '"' || true)
    echo "JAVA_VERSION=${JAVA_VERSION_INT_VALUE:-8}"
    echo "Using DEFAULT_JVM_OPTS variable with value '${DEFAULT_JVM_OPTS}'"
    echo "--------------------------------------------------------------------------------------------------------------"

    eval splitJvmOpts ${DEFAULT_JVM_OPTS} ${JAVA_OPTS:-} ${RECORD_OPTS:-}
    exec "$JAVACMD" "${JVM_OPTS[@]}" -jar "$JARFILE" ${PARAM_CONFIG_FILE} ${PARAM_STORE_DIR} ${PARAM_SOURCECODE_DIR} "$@"
}

#startRecorderViaCapsule $@

startRecorderViaPackr() {
    # Prepare params
    PARAM_CONFIG_FILE="--config ${APP_HOME}/config/credentials.config"
    PARAM_STORE_DIR="--store ${APP_HOME}/record/localstore"
    PARAM_SOURCECODE_DIR="--sourcecode ${APP_HOME}"

    # typically we would be downloading this via wget from githib releases page
    if [[ ! -s record-and-upload.zip ]]; then
        # wget --no-clobber --continue https://www.dropbox.com/s/09e0zxknz7ir4bi/record-and-upload.zip   <=== we would have done it in this manner using the Linux wget command
        ## wget is invoked in this manner due as wget depends on a Linux library and this library is a placed in the tools folder
        ## typically by setting java.library.path to the tools folder we could get this to work but for some reason it won't work
        cd tools && ./wget-${OSName} https://github.com/julianghionoiu/record-and-upload/releases/download/v0.0.16/record-and-upload-linux.zip ../record-and-upload.zip && cd ..

        ## if the above is failing, replace the download url above with the location to download record-and-upload.zip
    fi    

    if [[ -s record-and-upload.zip ]]; then
        ./tools/unzip -u -o record-and-upload.zip        
    else
        echo "Failed to find a valid record-and-upload.zip, either the downloading or unpacking of the zip file failed."
        exit -1
    fi

    if [[ -e record ]]; then
        ./record/record-and-upload ${PARAM_CONFIG_FILE} ${PARAM_STORE_DIR} ${PARAM_SOURCECODE_DIR} $@
    else
        echo "Failed to find the 'record' folder, unpacking of the zip file might have failed."
        exit -1        
    fi

    # JAVACMD=${APP_HOME}/record/jre/bin/java
    # chmod +x ${JAVACMD}
    # set -x
    # cd record && "$JAVACMD" -jar record-and-upload-capsule.jar -Dcapsule.log=verbose ${PARAM_CONFIG_FILE} ${PARAM_STORE_DIR} ${PARAM_SOURCECODE_DIR} $@ && cd ..
    # set +x
}

startRecorderViaPackr $@
