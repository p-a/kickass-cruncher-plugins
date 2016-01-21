// ByteBoozer Decruncher    /HCL May.2003
// B2..                      October 2014
// Short decruncher for execs    Dec 2014

.importonce

// Move decruncher to zeropage and execute
.macro B2ZERO(data, length, startAddr, relocate) {
    
    sei
    lda #$34
    sta $01
    ldx #decr_end - decr
  !:
    lda decr-1,x
    sta $02-1,x
    dex
    bne !-
    jmp $02
            
    decr:    
    .pseudopc $0002 {
      
      .if (relocate) {
        ldy #>[length+$ff]
      copy:  
        lda data+length-$100,x
        sta $ff00,x
        inx
        bne copy
        dec copy+2
        dec copy+5
        dey
        bne copy
      }
        
    :B2ZERO_DECRUNCH_AND_EXEC(
            relocate ? $10000 - length : data,
            startAddr)
    }
    decr_end:
}

.macro B2ZERO_DECRUNCH_AND_EXEC(data, startAddr) {

Decrunch:
    lda data
    sta.zp put
    lda data+1
    sta.zp put+1
DLoop:
    jsr GetNextBit
    bcs Match

    // Literal run.. get length.
    jsr GetLen
    sta.zp LLen+1 // sta LLen+1

    ldy #0
  !:  
    jsr GetNextByte
    sta (put),y
    iny
LLen:
    cpy #0
    bne !-

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
    stx.zp MLen+1 

    // Get num bits
    lda #0
    cpx #3
    rol
    jsr RolNextBit
    jsr RolNextBit
    tax
    lda.zpx Tab,x
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
    .byte LDX_ABS //$ae jmp Mdone
MShort:
    ldy #$ff
Mdone:

    // clc
    adc.zp put
    sta.zp MLda+1
    tya
    adc.zp put+1
    sta.zp MLda+2

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
    adc.zp put
    sta.zp put
    bcc !+
    inc.zp put+1
   !:
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
    asl.zp Bits
    bne DgEnd
    pha
    jsr GetNextByte
    rol
    sta.zp Bits
    pla
DgEnd:
    rts

GetNextByte:
    lda data+2
    inc.zp GetNextByte+1
    bne !+
    inc.zp GetNextByte+2
   !:
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

}