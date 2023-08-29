SRCFILES := $(shell find src -name \*.java)
PACKAGES := $(shell find src -name \*.java | sed -E s/^src\\/\|\\/[^\\/]+$$//g | grep -v \\.java$$ | sort | uniq | tr / .)

all: clean bin doc polyhedra.jar

eclipseall: eclipseclean polyhedra.jar

clean:
	rm -rf bin
	rm -rf doc
	rm -rf *.jar

eclipseclean:
	rm -rf *.jar

bin: src
	mkdir -p bin
ifeq ($(origin JAVACFLAGS), undefined)
	$(eval JAVACFLAGS = $(shell dep/minJavaVerOpts))
endif
	javac $(JAVACFLAGS) -sourcepath src $(SRCFILES) -d bin

doc: src
	mkdir -p doc
	javadoc $(JAVADOCFLAGS) -sourcepath src $(PACKAGES) -d doc

polyhedra.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST.MF polyhedra.jar -C bin com/kreative/polyhedra
	chmod +x polyhedra.jar

.PHONY: all eclipseall clean eclipseclean
