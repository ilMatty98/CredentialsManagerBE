package com.credentialsmanager.test.controller;

import com.credentialsmanager.controller.ControllerLoggingAspect;
import com.credentialsmanager.test.CredentialsManagerTests;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(OutputCaptureExtension.class)
class ControllerLoggingAspectTest extends CredentialsManagerTests {

    @InjectMocks
    protected ControllerLoggingAspect controllerLoggingAspect;

    @Test
    void testLogMethodStart(CapturedOutput output) {
        controllerLoggingAspect.logMethodStart(getJoinPoint(true));
        assertTrue(output.getOut().contains("Start method TEST TEST TEST"));
    }

    @Test
    void testLogMethodEnd(CapturedOutput output) {
        controllerLoggingAspect.logMethodEnd(getJoinPoint(false));
        assertTrue(output.getOut().contains("End method"));
    }

    private JoinPoint getJoinPoint(boolean signature) {
        return new JoinPoint() {
            @Override
            public String toShortString() {
                return null;
            }

            @Override
            public String toLongString() {
                return null;
            }

            @Override
            public Object getThis() {
                return null;
            }

            @Override
            public Object getTarget() {
                return null;
            }

            @Override
            public Object[] getArgs() {
                return new Object[0];
            }

            @Override
            public Signature getSignature() {
                return new Signature() {
                    @Override
                    public String toShortString() {
                        return signature ? "TEST TEST TEST" : null;
                    }

                    @Override
                    public String toLongString() {
                        return null;
                    }

                    @Override
                    public String getName() {
                        return null;
                    }

                    @Override
                    public int getModifiers() {
                        return 0;
                    }

                    @Override
                    public Class getDeclaringType() {
                        return null;
                    }

                    @Override
                    public String getDeclaringTypeName() {
                        return null;
                    }
                };
            }

            @Override
            public SourceLocation getSourceLocation() {
                return null;
            }

            @Override
            public String getKind() {
                return null;
            }

            @Override
            public StaticPart getStaticPart() {
                return null;
            }
        };
    }
}
