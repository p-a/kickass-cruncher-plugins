.plugin "se.triad.kickass.CruncherPlugins"

.pc = $0801 "demo"

:BasicUpstart(start)

.import source "b2_decrzero.inc"

.pc = $0810
start:
        sei
        lda #$34
        sta $01
        :B2ZERO(crunchedData, packend - crunchedData, entrypoint, true)        

crunchedData:
.modify B2() {
 .pc = $0810
 
 .const ZP_PLASMA = $50
 
  entrypoint:
        sei
        lda #$35
        sta $01
        
        lda #0
        sta $d020
        sta $d021
 
        lda #2
        sta $dd00
        
        lda #%01100110
        ldy #$20
        ldx #0
       !: 
        eor #%11111111
       .label store_hi = *+2 
        sta plasma.bitmap,x
        inx
        bne !-
        inc store_hi
        dey
        bne !-
        
        lda #$d8
        sta $d016
        lda #$80
        sta $d018
      !: 
        sta $d800,x
        sta $d900,x
        sta $da00,x
        sta $dae8,x
        inx
        bne !-
        
        lda #$3b
        sta $d011
   
    loop:
        bit $d011
        bpl loop
        jsr plasma.run
        jmp loop
 

   plasma: {

    .label screenbase = $6000
    .label bitmap = $4000
    .pc = $8000 "speedcode"

    run: {
        .var p2s = List()
      
        ldx #0
        .label p3 = *-1
        ldy #0
        .label p4 = *-1
        clc
        .for (var x=0; x < 40; x++){
            lda sinus+x,x
            adc sinus+$40+2*x,y
            sta.z ZP_PLASMA+x
        }
        
        ldx #0
        .label p1 = *-1
        stx $FF
        .for (var y=0; y < 25; y++){
            lda sinus+y,x
            adc sinus2+10+y
            .eval p2s.add(*-2)
            tax
           .for (var x=0; x < 40; x++){
                adc.z ZP_PLASMA+x
                tay
                lda colors,y
                sta screenbase+x+y*40
                txa
           }
           ldx $FF
        }
                
        inc p4
        inc p4
        inc p4

        inc p1
        inc p1
      
        inc p3
        
        .for (var i =0; i < p2s.size(); i++){
            dec p2s.get(i)
        }
        
        rts             
    }

    .align $0100
    sinus:
        .fill 512, round(31.5+31.5*sin(i*2*PI/256))
    sinus2:
        .fill 512, round(31.5+31.5*sin(i*2*PI/256)+63.5*cos(i*2*PI/256))
    colors:
        .for (var i = 0; i < 8; i++){
            .byte $00,$09,$99,$9b,$bb,$bc,$cc,$cf,$ff,$f7,$77,$7d,$dd,$d1,$11,$11
            .byte $11,$d1,$dd,$7d,$77,$f7,$ff,$cf,$cc,$bc,$bb,$9b,$99,$09,$00,0
        }
   }
}
packend: