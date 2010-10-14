/*
 Algorithm Name: Keccak
 Authors: Guido Bertoni, Joan Daemen, Michaël Peeters and Gilles Van Assche
 Date: October 27, 2008
 
 This code, originally by Guido Bertoni, Joan Daemen, Michaël Peeters and
 Gilles Van Assche as a part of the SHA-3 submission, is hereby put in the
 public domain. It is given as is, without any guarantee.
 
 For more information, feedback or questions, please refer to our website:
 http://keccak.noekeon.org/
 */

//#include <malloc.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "displayIntermediateValues.h"
#include "KeccakNISTInterface.h"
#include "KeccakPermutationInterface.h"
#include "KeccakPermutationReference.h"

int genKAT_main();

void displayPermutationIntermediateValues()
{
    unsigned char state[KeccakPermutationSizeInBytes];
    const char *fileName = "KeccakPermutationIntermediateValues.txt";
    FILE *f;
	
    f = fopen(fileName, "w");
    if (f == NULL)
        printf("Could not open %s\n", fileName);
    else {
        KeccakInitialize();
        fprintf(f, "+++ The round constants +++\n");
        fprintf(f, "\n");
        displayRoundConstants(f);
		
        fprintf(f, "+++ The rho offsets +++\n");
        fprintf(f, "\n");
        displayRhoOffsets(f);
		
        displaySetIntermediateValueFile(f);
        displaySetLevel(3);
		
        fprintf(f, "+++ Example with the all-zero input +++\n");
        fprintf(f, "\n");
        memset(state, 0, KeccakPermutationSizeInBytes);
        KeccakPermutation(state);
		
        fprintf(f, "+++ Example taking the previous output as input +++\n");
        fprintf(f, "\n");
        KeccakPermutation(state);
		
        fclose(f);
        displaySetIntermediateValueFile(0);
    }
}

void alignLastByteOnLSB(const char *in, unsigned char *out, unsigned int length)
{
    unsigned int lengthInBytes;
	
    lengthInBytes = (length+7)/8;
    memcpy(out, in, lengthInBytes);
    if ((length % 8) != 0)
        out[lengthInBytes-1] = out[lengthInBytes-1] >> (8-(length%8));
}

void displayMessageInInternalConvention(const char *message, unsigned int length)
{
    unsigned char *messageInternal;
	
    messageInternal = malloc((length+7)/8);
    alignLastByteOnLSB(message, messageInternal, length);
    displayBits(2, "Input message (in bits)", message, length, 1);
    displayBits(2, "Input message (in bits, after the formal bit reordering)", messageInternal, length, 0);
    displayBytes(2, "Input message (last byte aligned on LSB)", messageInternal, (length+7)/8);
    free(messageInternal);
}

void displaySpongeIntermediateValuesOne(const char *message, unsigned int messageLength, unsigned int digestSize)
{
    hashState state;
    unsigned char digest[512];
	
    displayBytes(1, "Input message (last byte aligned on MSB)", message, (messageLength+7)/8);
	
    displayMessageInInternalConvention(message, messageLength);
	
    Init(&state, digestSize);
    displayStateAsBytes(1, "Initial state", state.state);
    Update(&state, message, messageLength);
    Final(&state, digest);
    if (digestSize == 0) {
        displayText(1, "--- Switching to squeezing phase ---");
        Squeeze(&state, digest, 4096);
    }
}

void displaySpongeIntermediateValuesFew(FILE *f, unsigned int digestSize)
{
    const char *message1 = "\x53\x58\x7B\xC8";
    unsigned int message1Length = 29;
    const char *message2 = 
	"\x83\xAF\x34\x27\x9C\xCB\x54\x30\xFE\xBE\xC0\x7A\x81\x95\x0D\x30"
	"\xF4\xB6\x6F\x48\x48\x26\xAF\xEE\x74\x56\xF0\x07\x1A\x51\xE1\xBB"
	"\xC5\x55\x70\xB5\xCC\x7E\xC6\xF9\x30\x9C\x17\xBF\x5B\xEF\xDD\x7C"
	"\x6B\xA6\xE9\x68\xCF\x21\x8A\x2B\x34\xBD\x5C\xF9\x27\xAB\x84\x6E"
	"\x38\xA4\x0B\xBD\x81\x75\x9E\x9E\x33\x38\x10\x16\xA7\x55\xF6\x99"
	"\xDF\x35\xD6\x60\x00\x7B\x5E\xAD\xF2\x92\xFE\xEF\xB7\x35\x20\x7E"
	"\xBF\x70\xB5\xBD\x17\x83\x4F\x7B\xFA\x0E\x16\xCB\x21\x9A\xD4\xAF"
	"\x52\x4A\xB1\xEA\x37\x33\x4A\xA6\x64\x35\xE5\xD3\x97\xFC\x0A\x06"
	"\x5C\x41\x1E\xBB\xCE\x32\xC2\x40\xB9\x04\x76\xD3\x07\xCE\x80\x2E"
	"\xC8\x2C\x1C\x49\xBC\x1B\xEC\x48\xC0\x67\x5E\xC2\xA6\xC6\xF3\xED"
	"\x3E\x5B\x74\x1D\x13\x43\x70\x95\x70\x7C\x56\x5E\x10\xD8\xA2\x0B"
	"\x8C\x20\x46\x8F\xF9\x51\x4F\xCF\x31\xB4\x24\x9C\xD8\x2D\xCE\xE5"
	"\x8C\x0A\x2A\xF5\x38\xB2\x91\xA8\x7E\x33\x90\xD7\x37\x19\x1A\x07"
	"\x48\x4A\x5D\x3F\x3F\xB8\xC8\xF1\x5C\xE0\x56\xE5\xE5\xF8\xFE\xBE"
	"\x5E\x1F\xB5\x9D\x67\x40\x98\x0A\xA0\x6C\xA8\xA0\xC2\x0F\x57\x12"
	"\xB4\xCD\xE5\xD0\x32\xE9\x2A\xB8\x9F\x0A\xE1";
    unsigned int message2Length = 2008;
	
    fprintf(f, "+++ Example with a small message +++\n");
    fprintf(f, "\n");
    fprintf(f, "This is the message of length 29 from ShortMsgKAT.txt.\n");
    fprintf(f, "\n");
    displaySpongeIntermediateValuesOne(message1, message1Length, digestSize);
	
    fprintf(f, "+++ Example with a larger message +++\n");
    fprintf(f, "\n");
    fprintf(f, "This is the message of length 2008 from ShortMsgKAT.txt.\n");
    fprintf(f, "\n");
    displaySpongeIntermediateValuesOne(message2, message2Length, digestSize);
}

void displaySpongeIntermediateValues()
{
    const unsigned int digestSize[5] = {0, 224, 256, 384, 512};
    char fileName[256];
    FILE *f;
    unsigned int i;
	
    for(i=0; i<5; i++) {
        sprintf(fileName, "KeccakSpongeIntermediateValues_%d.txt", digestSize[i]);
        f = fopen(fileName, "w");
        if (f == NULL)
            printf("Could not open %s\n", fileName);
        else {
            displaySetIntermediateValueFile(f);
            displaySetLevel(2);
			
            displaySpongeIntermediateValuesFew(f, digestSize[i]);
			
            fclose(f);
            displaySetIntermediateValueFile(0);
        }
    }
}

int main()
{
    displayPermutationIntermediateValues();
    displaySpongeIntermediateValues();
    return genKAT_main();
}
