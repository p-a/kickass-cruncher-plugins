.importonce
// ByteBoozer Decruncher    /HCL May.2003
// KickAssified by Ruk/TRIAD

//define .const BB_ZP_BASE before import

.macro BB_DECRUNCH(addr){
            ldy #<addr
            ldx #>addr
            jsr byteboozer.Decrunch
}

.namespace byteboozer {

.label cpl	= BB_ZP_BASE+0 //1
.label cur	= BB_ZP_BASE+1 //1
.label zp	= BB_ZP_BASE+2 // -
.label put	= BB_ZP_BASE+2 //2
.label get	= BB_ZP_BASE+4 //2
.label cps	= BB_ZP_BASE+6 //2


Decrunch:
	sty get
	stx get+1

	ldy #2
	lda (get),y
	sta cur,y
	dey
	bpl *-6

	clc
	lda #3
	adc get
	sta get
	bcc *+4
	inc get+1

D_loop:
	asl cur
	bne *+5
	jsr D_get
Dl_1:
	php
	lda #1
Dl_2:
	asl cur
	bne *+5
	jsr D_get
	bcc Dl_2e
	asl cur
	bne *+5
	jsr D_get
	rol
	bpl Dl_2
Dl_2e:
	plp
	bcs D_copy

D_plain:
	sta cpl

	ldy #0
	lda (get),y
	sta (put),y
	iny
	cpy cpl
	bne *-7

	ldx #get-zp
	jsr D_add
	iny
	beq D_loop
	sec
	bcs Dl_1

D_copy:
	adc #0
	beq D_end
	sta cpl
	cmp #3

	lda #0
	sta cps
	sta cps+1

	rol
	asl cur
	bne *+5
	jsr D_get
	rol
	asl cur
	bne *+5
	jsr D_get
	rol
	tax
Dc_1s:
	ldy Tab,x
Dc_1:
	asl cur
	bne *+5
	jsr D_get
	rol cps
	rol cps+1
	dey
	bne Dc_1
	txa
	dex
	and #3
	beq Dc_1e
	inc cps
	bne Dc_1s
	inc cps+1
	bne Dc_1s
Dc_1e:
	sec
	lda put
	sbc cps
	sta cps
	lda put+1
	sbc cps+1
	sta cps+1

	lda (cps),y
	sta (put),y
	iny
	cpy cpl
	bne *-7

	ldx #put-zp
	jsr D_add
	jmp D_loop

D_get:
	sta Dg_a+1
	sty Dg_y+1
	ldy #0
	lda (get),y
	inc get
	bne *+4
	inc get+1
	sec
	rol
	sta cur
Dg_y:	ldy #0
Dg_a:	lda #0
D_end:
	rts

D_add:
	clc
	tya
	adc zp,x
	sta zp,x
	bcc *+4
	inc zp+1,x
	dex
	dex
	bpl D_add
	rts

Tab:
    .byte 4,2,2,2
	.byte 5,2,2,3
}