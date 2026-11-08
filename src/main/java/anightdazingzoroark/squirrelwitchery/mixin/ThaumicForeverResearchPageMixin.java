package anightdazingzoroark.squirrelwitchery.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.client.gui.GuiResearchPage;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//thaumic forever compat
@Mixin(value = GuiResearchPage.class, priority = 900, remap = false)
public abstract class ThaumicForeverResearchPageMixin {
    @Unique
    private Map<String, Method> squirrelWitchery$researchHooks;

    @WrapMethod(method = {"drawScreen(IIF)V", "func_73863_a(IIF)V"}, require = 1)
    private void squirrelWitchery$renderResearch(int mouseX, int mouseY, float partialTicks, Operation<Void> original) {
        squirrelWitchery$invokeResearchHook("oldresearch$pushRenderResearchPlayer", mouseX, mouseY, partialTicks);
        try {
            original.call(mouseX, mouseY, partialTicks);
        }
        catch (RuntimeException | Error exception) {
            squirrelWitchery$invokeResearchHook("oldresearch$popRenderResearchPlayer", mouseX, mouseY, partialTicks);
            throw exception;
        }
        squirrelWitchery$invokeResearchHook("oldresearch$popRenderResearchPlayer", mouseX, mouseY, partialTicks);
    }

    @WrapMethod(method = {"mouseClicked(III)V", "func_73864_a(III)V"}, require = 1)
    private void squirrelWitchery$clickResearch(int mouseX, int mouseY, int button, Operation<Void> original) {
        // Check the rendered note before starting a new context: the render handler clears old hitboxes.
        if (squirrelWitchery$invokeResearchHook("oldresearch$handleNoteClick", mouseX, mouseY)) return;

        // Keep the player's research mode available to the original prerequisite checks as well.
        squirrelWitchery$invokeResearchHook("oldresearch$pushRenderResearchPlayer", mouseX, mouseY, 0f);
        try {
            original.call(mouseX, mouseY, button);
        }
        catch (RuntimeException | Error exception) {
            squirrelWitchery$invokeResearchHook("oldresearch$popRenderResearchPlayer", mouseX, mouseY, 0f);
            throw exception;
        }
        squirrelWitchery$invokeResearchHook("oldresearch$popRenderResearchPlayer", mouseX, mouseY, 0f);
    }

    @Unique
    private boolean squirrelWitchery$invokeResearchHook(@Nonnull String hookName, @Nonnull Object... arguments) {
        if (squirrelWitchery$researchHooks == null) {
            squirrelWitchery$researchHooks = new HashMap<>();
            // These are third-party mixin methods. Resolve their generated names per screen so
            // Thaumic Forever remains optional and no reflection lookup runs on subsequent frames.
            for (Class<?> type = getClass(); type != Object.class; type = type.getSuperclass()) {
                for (Method method : type.getDeclaredMethods()) {
                    for (String name : List.of("oldresearch$pushRenderResearchPlayer", "oldresearch$popRenderResearchPlayer", "oldresearch$handleNoteClick")) {
                        if (!(method.getName().equals(name) || method.getName().endsWith("$" + name))) continue;
                        Class<?>[] parameters = method.getParameterTypes();
                        boolean supported;
                        if (name.equals("oldresearch$handleNoteClick")) {
                            supported = method.getReturnType() == boolean.class && Arrays.equals(parameters, new Class<?>[]{int.class, int.class});
                        } else {
                            supported = method.getReturnType() == void.class
                                    && (Arrays.equals(parameters, new Class<?>[]{int.class, int.class, float.class})
                                    || Arrays.equals(parameters, new Class<?>[]{int.class, int.class, float.class, CallbackInfo.class}));
                        }
                        if (supported) {
                            method.setAccessible(true);
                            squirrelWitchery$researchHooks.putIfAbsent(name, method);
                        }
                    }
                }
            }
            // The two render callbacks share a stack and must always be invoked as a pair.
            if (!squirrelWitchery$researchHooks.containsKey("oldresearch$pushRenderResearchPlayer")
                    || !squirrelWitchery$researchHooks.containsKey("oldresearch$popRenderResearchPlayer")
            ) {
                squirrelWitchery$researchHooks.remove("oldresearch$pushRenderResearchPlayer");
                squirrelWitchery$researchHooks.remove("oldresearch$popRenderResearchPlayer");
            }
        }

        Method method = squirrelWitchery$researchHooks.get(hookName);
        if (method == null) return false;

        Object[] invocationArguments = arguments;
        // CleanMix can remove an unused CallbackInfo parameter; support both handler forms.
        if (method.getParameterCount() == arguments.length + 1) {
            invocationArguments = Arrays.copyOf(arguments, arguments.length + 1);
            invocationArguments[arguments.length] = new CallbackInfo(hookName, false);
        }
        try {
            return Boolean.TRUE.equals(method.invoke(this, invocationArguments));
        }
        catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException runtimeException) throw runtimeException;
            if (cause instanceof Error error) throw error;
            throw new IllegalStateException("Thaumic Forever research hook failed: " + hookName, cause);
        }
        catch (IllegalAccessException exception) {
            throw new IllegalStateException("Cannot access Thaumic Forever research hook: " + hookName, exception);
        }
    }
}
