.section .text
.global foo
foo:
	push %ebp
	mov %esp, %ebp
	sub $8, %esp
	mov -4(%ebp), %eax
	leave
	ret
.global main
main:
	push %ebp
	mov %esp, %ebp
	sub $32, %esp
	push $
	call foo
	jmp KREG.3
	KREG.4:
	KREG.10:
	jmp KREG.13
	KREG.11:
	KREG.12:
	jmp KREG.13
	KREG.13:
	KREG.17:
	jmp KREG.18
	KREG.18:
	KREG.3:
	KREG.5:
	mov -8(%ebp), %eax
	leave
	ret
