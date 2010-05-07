LIB = lib%{lib}.so

INCLUDES = 
CFLAGS =
CXXFLAGS =
LDFLAGS =
LIBS =

SRCROOT = %{src}
OBJROOT = %{obj}
LIBROOT = libs/%{arch}

ANDROID_NDK_DIR = %{ndkDir}
ANDROID_HOST = %{host}
ANDROID_GCC_VER = %{gccVer}
ANDROID_PLAT = %{androidVer}

SOURCES = $(shell find $(SRCROOT) -name *.cpp -o -name *.c)

OBJS = \
	$(patsubst $(SRCROOT)/%.cpp, $(OBJROOT)/%.o, $(filter %.cpp, $(SOURCES))) \
	$(patsubst $(SRCROOT)/%.c, $(OBJROOT)/%.o, $(filter %.c, $(SOURCES)))

ANDROID_TOOLCHAIN = $(ANDROID_NDK_DIR)/build/prebuilt/$(ANDROID_HOST)/arm-eabi-$(ANDROID_GCC_VER)/bin
ANDROID_PREFIX = $(ANDROID_TOOLCHAIN)/arm-eabi-

ANDROID_SYSROOT = --sysroot=$(ANDROID_NDK_DIR)/build/platforms/$(ANDROID_PLAT)/arch-arm

ANDROID_CFLAGS = $(ANDROID_SYSROOT) \
	-fpic -mthumb-interwork -ffunction-sections -funwind-tables -fstack-protector -fno-short-enums \
	-D__ARM_ARCH_5__ -D__ARM_ARCH_5T__ -D__ARM_ARCH_5E__ -D__ARM_ARCH_5TE__ \
	-march=armv5te -mtune=xscale -msoft-float -mthumb -Os \
	-fomit-frame-pointer -fno-strict-aliasing -finline-limit=64 \
	-D__ANDROID__ -DANDROID -O2 -DNDEBUG -g

ANDROID_CXXFLAGS = $(ANDROID_CFLAGS) -fno-exceptions -fno-rtti

all:	$(LIBROOT)/$(LIB)

$(OBJROOT)/$(LIB):	$(OBJS)
	$(ANDROID_PREFIX)gcc -nostdlib $(ANDROID_SYSROOT) -Wl,-shared,-Bsymbolic -Wl,--no-undefined -o $@ \
		$^ $(LIBS) -lc -lm $(ANDROID_TOOLCHAIN)/../lib/gcc/arm-eabi/$(ANDROID_GCC_VER)/libgcc.a

$(LIBROOT)/$(LIB):	$(OBJROOT)/$(LIB)
	@[ -d $(dir $@) ] || mkdir -p $(dir $@)
	$(ANDROID_PREFIX)strip --strip-debug -o $@ $^

$(OBJROOT)/%.o:	$(SRCROOT)/%.cpp
	@[ -d $(dir $@) ] || mkdir -p $(dir $@)
	$(ANDROID_PREFIX)g++ $(ANDROID_CXXFLAGS) $(CXXFLAGS) $(INCLUDES) -MD -o $@ -c $<

$(OBJROOT)/%.o:	$(SRCROOT)/%.c
	@[ -d $(dir $@) ] || mkdir -p $(dir $@)
	$(ANDROID_PREFIX)gcc $(ANDROID_CFLAGS) $(CFLAGS) $(INCLUDES) -MD -o $@ -c $<

-include $(OBJS:%.o=%.d)

clean:
	rm -fr $(OBJROOT)/*
