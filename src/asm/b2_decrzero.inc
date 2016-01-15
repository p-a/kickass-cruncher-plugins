// ByteBoozer Decruncher    /HCL May.2003
// B2..                      October 2014
// Short decruncher for execs    Dec 2014

.importonce

// Move decruncher to zeropage and execute
.macro B2ZERO(data, startVector) {
    
    ldx #decr_end - decr
  !:
    lda decr-1,x
    sta $02-1,x
    dex
    bne !-
    jmp $02
            
    decr:    
    .pseudopc $0002 {
        :B2ZERO_DECRUNCH_AND_EXEC(data, startVector)
    }
    decr_end:
}

.macro B2ZERO_DECRUNCH_AND_EXEC(data, startVector) {

Decrunch:
DLoop:
    jsr GetNextBit
    bcs Match

    // Literal run.. get length.
    jsr GetLen
    sta LLen+1 // sta LLen+1

    ldy #0
    jsr GetNextByte
    sta (put),y
    iny
LLen:
    cpy #0
    bne *-8

    jsr AddPut

    iny
    beq DLoop

    // Has to continue with a match..

Match:
    // Match.. get length.
    jsr GetLen

    // Length 255 -> EOF
    tax
    inx
    beq End
    stx MLen+1 

    // Get num bits
    lda #0
    cpx #3
    rol
    jsr RolNextBit
    jsr RolNextBit
    tax
    lda Tab,x
    beq M8

    // Get bits < 8
    jsr RolNextBit
    bcs *-3
    bmi MShort
M8:
    // Get byte
    eor #$ff
    tay
    jsr GetNextByte
    .byte BIT_ABS //jmp Mdone
MShort:
    ldy #$ff
Mdone:

    // clc
    adc put
    sta mLda+1
    tya
    adc put+1
    sta mLda+2

    ldy #0
MLda:
    lda $b00b,y
.label put = *+1
    sta $babe,y
    iny
MLen:
    cpy #0
    bne MLda

    jsr AddPut

    bne DLoop

AddPut:
    clc
    tya
    adc put
    sta put
    bcc *+4
    inc put+1
    rts

GetLen:
    lda #1
GlLoop:
    jsr GetNextBit
    bcc GlEnd
    jsr RolNextBit
    bpl GlLoop
GlEnd:
    rts

RolNextBit:
    jsr GetNextBit
    rol
    rts

GetNextBit:
    asl Bits
    bne DgEnd
    pha
    jsr GetNextByte
    rol
    sta Bits
    pla
DgEnd:
    rts

GetNextByte:
    lda crunchedBytes
    inc GetNextByte+1
    bne *+4
    inc GetNextByte+2
    rts
End:
    jmp startAddr

Bits:
    .byte $80

Tab:
    // Short offsets
    .byte %11011111 // 3
    .byte %11111011 // 6
    .byte %00000000 // 8
    .byte %10000000 // 10
    // Long offsets
    .byte %11101111 // 4
    .byte %11111101 // 7
    .byte %10000000 // 10
    .byte %11110000 // 13
    
.if ( * > $ff ) {
    .print "WARNING: This code was intended to reside in zeropage"
}