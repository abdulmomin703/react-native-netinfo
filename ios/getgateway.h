//
//  getgateway.h
//
//  This is pulled directly from https://stackoverflow.com/a/29440193/1120802
//

#ifdef __cplusplus
extern "C" {
#endif

#ifndef getgateway_h
#define getgateway_h

#include <stdio.h>

int getdefaultgateway(in_addr_t * addr);

#endif /* getgateway_h */

#ifdef __cplusplus
}
#endif
