.plugin "se.triad.kickass.CruncherPlugins"

.const KOALA_TEMPLATE = "C64FILE, Bitmap=$0000, ScreenRam=$1f40, ColorRam=$2328, BackgroundColor = $2710"
.var picture = LoadBinary("res/test.koa", KOALA_TEMPLATE)

.var music = LoadSid("res/Ruk - J.sid")

.pc = $0801
:BasicUpstart(start)

.pc = $0810
start:
    sei
    lda #$35
    sta $01
    lda #0
    sta $d020
    sta $d011
    :EXO_DECRUNCH(crunchedBitmapAndScreen)
    :EXO_DECRUNCH(crunchedColorRam)
    :EXO_DECRUNCH(crunchedMusic)
    lda #picture.getBackgroundColor()
    sta $d021
    lda #$d8
    sta $d016
    lda #2
    sta $dd00
    lda #$80
    sta $d018
    lda #0
    tax
    tay
    jsr music.init
    lda #$3b
    sta $d011
loop:    
    bit $d011
    bpl *-3
    bit $d011
    bmi *-3
    jsr music.play
    jmp loop
    
.const EXO_LITERAL_SEQUENCES_USED = true
.const EXO_ZP_BASE = $02
.const EXO_DECRUNCH_TABLE = $0200
.import source "exodecr.inc"    

.modify MemExomizer(false,true) {
    .pc = $4000 "Bitmap"
    .fill picture.getBitmapSize(), picture.getBitmap(i)
    .pc = $6000 "Screendata"
    .fill picture.getScreenRamSize(), picture.getScreenRam(i)
}
.label crunchedBitmapAndScreen = *

.modify MemExomizer(false,true) {
    .pc = $d800 "color-ram"
    .fill picture.getColorRamSize(), picture.getColorRam(i)
}
.label crunchedColorRam = *

.modify BackwardMemExomizer(*) {
    .pc = music.location "Music"
    .fill music.size, music.getData(i)
}
.label crunchedMusic = *

